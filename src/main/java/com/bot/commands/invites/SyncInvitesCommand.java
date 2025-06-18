package com.bot.commands.invites;

import com.bot.db.entities.GuildInviteEntity;
import com.bot.service.InviteService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CooldownScope;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class SyncInvitesCommand extends Command {

    @Autowired
    private InviteService inviteService;

    public SyncInvitesCommand() {
        this.name = "syncinvites";
        this.help = "Manually syncs all invite data in this server to the database";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
        this.cooldownScope = CooldownScope.GUILD;
        this.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        var invites = commandEvent.getGuild().retrieveInvites().complete();
        var inviteEntities = inviteService.getMapForGuildId(commandEvent.getGuild().getId());

        for (Invite invite : invites) {
            var entity = inviteEntities.remove(invite.getCode());
            if (entity == null) {
                // add existing if we dont have it
                inviteService.addExisting(invite);
            } else {
                // update count
                entity.setUses(invite.getUses());
                entity.setMaxUses(invite.getMaxUses());

                // These fields are static so only set if the values are null
                if (entity.getAuthor() == null) {
                    entity.setAuthor(invite.getInviter().getId());
                }
                if (entity.getCreated() == null) {
                    entity.setCreated(Timestamp.from(invite.getTimeCreated().toInstant()));
                }
                if (entity.getMaxAge() == null) {
                    entity.setMaxAge(invite.getMaxAge());
                }
                inviteService.save(entity);
            }
        }

        // Remove all invites no longer in guild
        for (GuildInviteEntity entity : inviteEntities.values()) {
            inviteService.remove(entity);
        }

        commandEvent.reactSuccess();
    }
}
