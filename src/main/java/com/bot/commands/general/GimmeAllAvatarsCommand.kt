package com.bot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class GimmeAllAvatarsCommand: Command() {

    init {
        this.name = "gimmeallavatars"
        this.ownerCommand = true
    }

    override fun execute(p0: CommandEvent?) {
        for (m in p0!!.guild.members) {
            p0.reply(m.user.avatarUrl)
        }
    }
}