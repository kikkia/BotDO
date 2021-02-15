package com.bot.commands.events

import com.bot.service.EventService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class AddEventCommand(val eventService: EventService) : Command() {
    init {
        name = "addevent"
        help = "Adds an event"
    }

    override fun execute(p0: CommandEvent?) {
        TODO("Not yet implemented")
        
    }
}