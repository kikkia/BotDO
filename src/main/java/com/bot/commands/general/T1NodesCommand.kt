package com.bot.commands.general

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.springframework.stereotype.Component

@Component
class T1NodesCommand : SlashCommand() {

    private val imgLink = "https://cdn.discordapp.com/attachments/712015240883077171/805878148155310140/unknown.png"

    init {
        this.name = "t1nodes"
        this.help = "Posts a list of all T1 nodes"
    }

    override fun execute(p0: SlashCommandEvent?) {
        p0!!.reply(imgLink).queue()
    }

    override fun execute(event: CommandEvent?) {
        event!!.reply(imgLink)
    }
}