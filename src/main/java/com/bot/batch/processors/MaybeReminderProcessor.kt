package com.bot.batch.processors

import com.bot.db.entities.WarEntity
import com.bot.models.WarReminder
import com.bot.utils.FormattingUtils
import org.springframework.batch.item.ItemProcessor
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

class MaybeReminderProcessor : ItemProcessor<List<WarEntity>, List<WarReminder>> {

    override fun process(wars: List<WarEntity>): List<WarReminder>? {
        val remindersToSend = ArrayList<WarReminder>()
        for (war in wars) {
            if (war.archived ||
                war.past() ||
                war.warTime.after(Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)))) {
                continue
            }
            // War is within next day, dm all maybes to ask if they are coming
            for (attendee in war.attendees) {
                if (attendee.maybe) {
                    remindersToSend.add(WarReminder(war.guild.discordGuild!!.id,
                        attendee.user.id,
                        war,
                        FormattingUtils.generateDmWarMaybeReminder(war)))
                }
            }
        }
        return remindersToSend
    }
}