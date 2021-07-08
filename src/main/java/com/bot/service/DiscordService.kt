package com.bot.service

import com.bot.db.mapper.GuildDiscordMapper
import com.bot.exceptions.api.GuildNotFoundException
import com.bot.models.GuildDiscord
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class DiscordService(private val shardManager: ShardManager,
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
                            guilds.add(GuildDiscordMapper.map(guildEntity))
                        }
                    }
                }
            }
        }
        return guilds
    }

    fun canUserAdminGuild(userId: String, guildId: String) : Boolean {
        var guild: Guild? = null
        for (shard in shardManager.shards) {
            guild = shard.getGuildById(guildId)
            if (guild != null)
                break
        }
        if (guild == null) {
            throw GuildNotFoundException("Guild not found")
        }
        val member = guild.getMemberById(userId)
        return if (member == null) false else canManageBot(member)
    }


    private fun canManageBot(member: Member) : Boolean {
        return member.hasPermission(Permission.MANAGE_SERVER)
    }
}