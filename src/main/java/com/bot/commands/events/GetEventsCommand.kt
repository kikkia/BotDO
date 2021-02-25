package com.bot.commands.events

import com.bot.service.EventService
import com.bot.utils.FormattingUtils
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class GetEventsCommand(private val eventService: EventService) : Command() {

    init {
        this.name = "events"
        this.help = "Gets all upcoming events in this guild."
    }

    override fun execute(command: CommandEvent?) {
        val events = eventService.getAllUpcomingByGuildId(command!!.guild.id)

        if (events.isEmpty()) {
            command.replyWarning("There are no upcoming events.")
            return
        }

        for ( e in events) {
            command.reply(e.name + " " + FormattingUtils.prettyPrintDuration(e.getDurationUntilEvent()))
        }
        val image = FormattingUtils.eventsToImage(events)
        command.reply(image, image.name)
    }
}