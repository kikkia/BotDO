package com.bot.utils

import com.bot.db.entities.GuildEntity
import com.bot.service.TextChannelService
import com.bot.service.WarService
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*

class WarUtils {

    companion object {
        fun sendNewWar(guild: GuildEntity, date: Date, channel: TextChannel, textChannelService: TextChannelService, warService: WarService) {
            val warMessage = channel.sendMessage("Generating War...").complete()
            val channelEntity = textChannelService.getById(channel.id)

            val war = warService.createWar(date.toInstant(), warMessage.id, channelEntity, guild.bdoGuild!!);
            // TODO: Guild war completion
            // TODO: War reminders
            // TODO: Archive channel
            // TODO: Stats
            warMessage.editMessage(FormattingUtils.generateWarMessage(war)).complete()
            for (reaction in Constants.WAR_REACTIONS) {
                warMessage.addReaction(reaction).complete()
            }
        }
    }
}