package com.bot.utils

import com.bot.db.entities.*
import com.bot.service.WarService
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*

class WarUtils {

    companion object {
        fun sendNewWar(guild: GuildEntity, date: Date, channel: TextChannel, channelEntity: TextChannelEntity, warService: WarService) {
            val warMessage = channel.sendMessage("Generating War...").complete()

            val war = warService.createWar(date.toInstant(), warMessage.id, channelEntity, guild.bdoGuild!!);
            // TODO: War reminders
            warMessage.editMessageEmbeds(FormattingUtils.generateWarMessage(war)).complete()
            for (reaction in Constants.WAR_REACTIONS) {
                warMessage.addReaction(reaction).complete()
            }
        }

        fun sendArchivedWar(war: WarEntity, vods: List<WarVodEntity>, stats: List<WarStatsEntity>, channel: TextChannel) : String {
            val warMessage = channel.sendMessage("Archiving War...").complete()

            warMessage.editMessageEmbeds(FormattingUtils.generateArchivedWarMessage(war,
                    vods,
                    stats)).complete()
            warMessage.addReaction(Constants.WAR_REACTION_REFRESH).complete()
            return warMessage.id
        }
    }
}