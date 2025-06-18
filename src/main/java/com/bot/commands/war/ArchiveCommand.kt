package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.service.*
import com.bot.utils.WarUtils
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Component
class ArchiveCommand(private val warService: WarService,
                     guildService: GuildService,
                     private val textChannelService: TextChannelService,
                     private val warStatsService: WarStatsService,
                     private val warVodService: WarVodService) : WarCommand(guildService) {
    init {
        this.name = "archive"
        this.help = "Archives the war in the channel and moves it to the archive channel (If no archive channel is not " +
                "setup, keep it in this channel), if the war was on a war day it makes the signup for the next war."
        this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()
        this.guildOnly = true
    }
    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        val wars = warService.getAllByChannelDoneAndActive(command.channel.id)
        if (wars.isEmpty()) {
            command.replyWarning("No active wars found in this channel, this command needs to be used in a channel with recently completed wars.")
            return
        }

        var archiveChannel : TextChannel? = null
        if (guild.archiveChannel == null) {
            val message = command.channel.sendMessage("No archive channel set, this war will stay in this channel.").complete()
            message.delete().queueAfter(10, TimeUnit.SECONDS) // Delete warning message
        } else {
            archiveChannel = command.guild.getTextChannelById(guild.archiveChannel!!) as TextChannel
            if (archiveChannel == null) {
                command.replyWarning("Archive channel not found! Please make sure your archive channel is correctly setup and try again.")
                return
            }
        }

        // Archive all still active and completed (past war time) wars in the channel
        for (war in wars) {
            val vods = warVodService.getAllByWarId(war.id)
            val stats = warStatsService.getAllByWarId(war.id)
            val channel = archiveChannel ?: command.guild.getTextChannelById(command.channel.id)
            command.channel.deleteMessageById(war.messageId).complete()
            warService.setMessageId(war, WarUtils.sendArchivedWar(war, vods, stats, channel!!))
            warService.setChannel(war, channel.id)
            // Generate new war signup if its a normal war day
            if (guild.bdoGuild!!.getWarDays().contains(war.getWarDay())) {
                val newDate = war.warTime.toInstant().plus(7, ChronoUnit.DAYS)
                WarUtils.sendNewWar(guild,
                        Timestamp.from(newDate),
                        command.guild.getTextChannelById(command.channel.id)!!,
                        textChannelService.getById(command.channel.id),
                        warService)
            }
            warService.setArchived(war)
        }
        command.message.delete().queue()
    }
}