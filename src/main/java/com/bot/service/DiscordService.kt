package com.bot.service

import com.bot.db.entities.WarEntity
import com.bot.db.mapper.GuildDiscordMapper
import com.bot.exceptions.api.GuildNotFoundException
import com.bot.models.GuildDiscord
import com.bot.utils.Constants
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.*

@Service
class DiscordService(private val shardManager: ShardManager,
                     private val guildService: GuildService,
                     private val warService: WarService) {

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

    fun getRoleInGuild(guildId: String, roleId: String) : Role? {
        val guild = getGuild(guildId)
        return if (guild == null) guild else guild.getRoleById(roleId)
    }

    fun getUserById(userId: String) : Optional<User> {
        var retUser: User? = null
        for (shard in shardManager.shards) {
            val user = shard.getUserById(userId)
            if (user == null) {
                continue
            } else {
                retUser = user
                break
            }
        }
        return Optional.ofNullable(retUser)
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

    fun sendDmMessage(war: WarEntity, user: User, message: String) : String {
        val privateChannel = user.openPrivateChannel().complete()
        val discMessage = privateChannel.sendMessage(message).complete()
        for (reaction in Constants.WAR_REACTIONS) {
            discMessage.addReaction(reaction).queue()
        }
        warService.addDmSignupMessage(war, discMessage.id, user.id)
        return user.id
    }


    private fun canManageBot(member: Member) : Boolean {
        return member.hasPermission(Permission.MANAGE_SERVER)
    }

    private fun getGuild(guildId: String) : Guild? {
        for (shard in shardManager.shards) {
            val guild = shard.getGuildById(guildId)
            if (guild == null) {
                continue
            } else {
                return guild
            }
        }
        return null
    }
}