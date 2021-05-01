package com.bot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class T1NodesCommand : Command() {

    init {
        this.name = "t1nodes"
        this.help = "Posts a list of all T1 nodes"
    }

    override fun execute(p0: CommandEvent?) {
        p0!!.reply("https://cdn.discordapp.com/attachments/712015240883077171/805878148155310140/unknown.png")
    }
}