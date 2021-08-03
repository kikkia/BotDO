package com.bot.batch.writers

import com.bot.models.WarReminder
import com.bot.service.DiscordService
import org.springframework.batch.item.ItemWriter

class WarReminderWriter(private val discordService: DiscordService) : ItemWriter<List<WarReminder>> {
    override fun write(reminderLists: MutableList<out List<WarReminder>>) {
        for (reminders in reminderLists) {
            for (reminder in reminders) {
                val userOpt = discordService.getUserById(reminder.user)
                if (userOpt.isEmpty) {
                    continue
                }
                discordService.sendDmMessage(reminder.war, userOpt.get(), reminder.message)
            }
        }
    }


}