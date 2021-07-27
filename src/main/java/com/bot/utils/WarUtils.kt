package com.bot.utils

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.WarEntity
import com.bot.db.entities.WarStatsEntity
import com.bot.db.entities.WarVodEntity
import com.bot.service.WarService
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*

class WarUtils {

    companion object {
        fun sendNewWar(guild: GuildEntity, date: Date, channel: TextChannel, channelEntity: com.bot.db.entities.TextChannelEntity, warService: WarService) {
            val warMessage = channel.sendMessage("Generating War...").complete()

            val war = warService.createWar(date.toInstant(), warMessage.id, channelEntity, guild.bdoGuild!!);
            // TODO: War reminders
            warMessage.editMessage(FormattingUtils.generateWarMessage(war)).complete()
            for (reaction in Constants.WAR_REACTIONS) {
                warMessage.addReaction(reaction).complete()
            }
        }

        fun sendArchivedWar(war: WarEntity, vods: List<WarVodEntity>, stats: List<WarStatsEntity>, channel: TextChannel) : String {
            val warMessage = channel.sendMessage("Archiving War...").complete()

            warMessage.editMessage(FormattingUtils.generateArchivedWarMessage(war,
                    vods,
                    stats)).complete()
            warMessage.addReaction(Constants.WAR_REACTION_REFRESH).complete()
            return warMessage.id
        }
    }
}