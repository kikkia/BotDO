package com.bot.commands.server

import com.bot.commands.RequiredArgsCommand
import com.bot.service.VoiceChannelService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class AddWarVoiceChannelCommand(private val voiceChannelService: VoiceChannelService) : Command() {
    init {
        this.name = "addwarvc"
        this.help = "Adds another war voice channel. (You must be in it)"
        this.userPermissions = arrayOf(Permission.MANAGE_SERVER)
    }

    override fun execute(commandEvent: CommandEvent?) {
        if (!commandEvent!!.member.voiceState!!.inVoiceChannel()) {
            commandEvent.replyWarning("You need to join a voice channel and use this command to mark that channel as a war channel.")
            return
        }

        val channelOpt = voiceChannelService.getById(commandEvent.member.voiceState!!.channel!!.id)
        if (channelOpt.isEmpty) {
            commandEvent.replyError("Something went wrong marking this channel as a war channel. Please try again later.")
            return
        }
        val channel = channelOpt.get()
        channel.war = true
        voiceChannelService.save(channel)
        commandEvent.reactSuccess()
    }
}