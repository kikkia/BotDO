package com.bot.tasks

import com.bot.db.entities.GuildEntity
import com.bot.service.InviteService
import com.bot.utils.FormattingUtils.generateWelcomeMessage
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import java.util.stream.Collectors

class InvitedMemberTask(private val event: GuildMemberJoinEvent,
                        private val inviteService: InviteService,
                        private val guild: GuildEntity) : Thread() {

    override fun run() {
        // Check incoming user for invite
        val invites = event.guild.retrieveInvites().complete();
        val inviteEntities = inviteService.getMapForGuildId(event.guild.id);

        for (invite in invites) {
            val entity = inviteEntities[invite.code];
            if (entity == null) {
                // We dont have this invite in our system, we cannot determine if this one was used
                inviteService.addExisting(invite);
                continue;
            }

            if (invite.uses > entity.uses) {
                // This invite was used as the count incremented
                entity.uses = invite.uses
                inviteService.save(entity)

                // Add role if one exists to add
                val roleIdsToAdd = entity.roles.stream().map { it.roleId }.collect(Collectors.toList())
                for (roleId in roleIdsToAdd) {
                    val roleToAdd = event.guild.getRoleById(roleId) ?: continue
                    event.guild.addRoleToMember(event.member, roleToAdd).complete()
                }

                // Add guild name if exists
                if (entity.guildPrefix != null) {
                    event.member.modifyNickname("(${entity.guildPrefix}) ${event.member.effectiveName}").queue()
                }

                // Post welcome message if exists
                if (entity.welcomeMessage != null &&
                        guild.welcomeChannel != null) {
                    val welcomeChannel = event.guild.getTextChannelById(guild.welcomeChannel!!)
                    welcomeChannel!!.sendMessage(generateWelcomeMessage(event, entity.welcomeMessage)).queue()
                }
                return
            }
        }
    }
}