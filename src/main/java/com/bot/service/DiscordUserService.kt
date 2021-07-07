package com.bot.service

import com.bot.db.mapper.GuildDiscordMapper
import com.bot.models.GuildDiscord
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Service

@Service
class DiscordUserService(private val shardManager: ShardManager,
                         private val guildService: GuildService) {

    fun getGuildsForUser(userId: String) : List<GuildDiscord> {
        val guilds = mutableListOf<GuildDiscord>()
        for (shard in shardManager.shards) {
            val user = shard.getUserById(userId)
            if (user == null) {
                continue
            }
            else {
                for (guild in user.mutualGuilds) {
                    if (canManageBot(guild.getMemberById(userId)!!)) {
                        val guildEntity = guildService.getById(guild.id)
                        if (guildEntity.bdoGuild != null) {
                            guilds.add(GuildDiscordMapper.map(guildEntity, guild.iconUrl))
                        }
                    }
                }
            }
        }
        return guilds
    }


    private fun canManageBot(member: Member) : Boolean {
        return member.hasPermission(Permission.MANAGE_SERVER)
    }
}