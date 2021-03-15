package com.bot.tasks

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.GuildInviteEntity
import com.bot.service.InviteService
import com.bot.service.UserService
import com.bot.utils.FormattingUtils.generateWelcomeMessage
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import java.time.Instant
import java.util.stream.Collectors

class InvitedMemberTask(private val event: GuildMemberJoinEvent,
                        private val inviteService: InviteService,
                        private val userService: UserService,
                        private val guild: GuildEntity) : Thread() {

    override fun run() {
        // Check incoming user for invite
        val invites = event.guild.retrieveInvites().complete();
        val inviteEntities = inviteService.getMapForGuildId(event.guild.id);
        val possibleInvites = ArrayList<GuildInviteEntity>()

        for (invite in invites) {
            val entity = inviteEntities.remove(invite.code);
            if (entity == null) {
                // We dont have this invite in our system, we cannot determine if this one was used
                inviteService.addExisting(invite);
                continue;
            }

            if (invite.uses > entity.uses) {
                // This invite was used as the count incremented
                entity.uses = invite.uses
                inviteService.save(entity)

                possibleInvites.add(entity)
            }
        }

        // Could be due to an invite that hit limit and disappeared when they used it
        // Check the invites that are only in our db and not in discord
        // If we find one that is 1 away from max, then choose that one
        // Remove all defunct entities
        if (possibleInvites.size == 0) {
            for (entity in inviteEntities.values) {

                if (entity.maxUses != null && !entity.isExpired()) {
                    if (entity.maxUses!! - entity.uses == 1) {
                        entity.uses++
                        possibleInvites.add(entity)
                    }
                }
                inviteService.remove(entity)
            }
        }

        if (possibleInvites.size != 1) {
            if (guild.logChannel != null) {
                logErrorState(possibleInvites, event)
            }
        } else {
            val entity = possibleInvites[0]
            // Its only 1 invite possible
            // Add role if one exists to add
            val roleIdsToAdd = entity.roles.stream().map { it.roleId }.collect(Collectors.toList())
            for (roleId in roleIdsToAdd) {
                val roleToAdd = event.guild.getRoleById(roleId) ?: continue
                event.guild.addRoleToMember(event.member, roleToAdd).complete()
            }

            // Add guild name if exists
            if (entity.guildPrefix != null) {
                try {
                    event.member.modifyNickname("(${entity.guildPrefix}) ${event.member.effectiveName}").queue()
                } catch (e: InsufficientPermissionException) {
                    if (guild.logChannel != null) {
                        val logChannel = event.guild.getTextChannelById(guild.logChannel!!)
                        logChannel!!.sendMessage("Failed to change nickname of new user due to lacking permissions.").queue()
                    }
                }
            }

            // Post welcome message if exists
            if (entity.welcomeMessage != null &&
                    guild.welcomeChannel != null) {
                val welcomeChannel = event.guild.getTextChannelById(guild.welcomeChannel!!)
                welcomeChannel!!.sendMessage(generateWelcomeMessage(event, entity.welcomeMessage)).queue()
            }

            // Post log of joining member
            if (guild.logChannel != null) {
                val logChannel = event.guild.getTextChannelById(guild.logChannel!!)
                logChannel!!.sendMessage("New member ${event.member.effectiveName} joined with invite ${entity.code}.\n" +
                        "```${guildInviteEntityToString(entity, event)}```").queue()
            }
        }
    }

    /**
     * Logs an error message to users when we cant determine which invite was used.
     */
    private fun logErrorState(possibleInvites: List<GuildInviteEntity>, event: GuildMemberJoinEvent) {
        /*
         We were unable to determine what invite was used for sure. This has a few common causes:
         1. Invite was not created with bot and was not in db.
         2. Our use counts were out of sync with reality. (Possible from down time or missed/errored join event)
         */
        val logChannel = event.guild.getTextChannelById(guild.logChannel!!)
        var message = "New member ${event.member.effectiveName} joined. However I was unable to determine the invite used. "
        message += if (possibleInvites.isEmpty()) {
            "This could be due to them using an invite I did not create."
        } else {
            "I identified multiple invites they could have used: " +
                    "```${possibleInvites.stream().map { it.code }.collect(Collectors.toList())}``` " +
                    "This can happen due to members joining during bot downtime. It should resolve itself. If not, please" +
                    " contact Kikkia."
        }
        logChannel!!.sendMessage(message).queue()
    }

    /**
     * Converts a guild invite entity to an easily readable object
     */
    private fun guildInviteEntityToString(entity: GuildInviteEntity, event: GuildMemberJoinEvent) : String {
        var out = "code: ${entity.code}\nUses: ${entity.uses}/${entity.maxUses}\n"
        if (entity.created != null) {
            out += "Created: ${entity.created}\n"
        }
        if (entity.author != null) {
            val author = userService.getById(entity.author)
            // Check if that user is in our db
            if (author != null) {
                out += "Inviter: ${author.getEffectiveName()}\n";
            }
        }
        if (entity.guildPrefix != null) {
            out += "Guild: ${entity.guildPrefix}\n"
        }
        if (entity.welcomeMessage != null) {
            out += "Welcome message: ${entity.welcomeMessage}\n"
        }
        if (entity.roles.isNotEmpty()) {
            // Map our role Ids from the entity to role names and filter out not found roles
            val assignedRoles = entity.roles.stream().map { event.guild.getRoleById(it.roleId) }
                    .filter{ it != null}
                    .map { it?.name }
                    .collect(Collectors.toList())
            out += "Assigned Roles: $assignedRoles"
        }
        return out
    }
}