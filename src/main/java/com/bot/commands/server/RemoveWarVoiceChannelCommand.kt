package com.bot.commands.server

import com.bot.service.VoiceChannelService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class RemoveWarVoiceChannelCommand(private val voiceChannelService: VoiceChannelService) : Command() {
    init {
        this.name = "removewarvc"
        this.help = "Remove a war voice channel. (Must be in the channel when using command)"
        this.userPermissions = arrayOf(Permission.MANAGE_SERVER)
    }

    override fun execute(command: CommandEvent?) {
        if (!command!!.member.voiceState!!.inVoiceChannel()) {
            command.replyWarning("You need to join a voice channel and use this command to mark that channel as a non-war channel.")
            return
        }

        val channelOpt = voiceChannelService.getById(command.member.voiceState!!.channel!!.id)
        if (channelOpt.isEmpty) {
            command.replyError("Something went wrong marking this channel as a non-war channel. Please try again later.")
            return
        }
        val channel = channelOpt.get()
        channel.war = false
        voiceChannelService.save(channel)
        command.reactSuccess()
    }
}