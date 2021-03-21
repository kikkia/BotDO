package com.bot.commands.server;

import com.bot.db.entities.GuildEntity;
import com.bot.service.GuildService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetRecruitRoleCommand extends Command {

    @Autowired
    private GuildService guildService;

    public SetRecruitRoleCommand() {
        this.name = "setrecruitrole";
        this.help = "Sets the role for new members of the guild";
        this.arguments = "Name or ID of role";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
            guildService.setRecruitRole(guild, null);
            commandEvent.reactSuccess();
            return;
        }

        Role role = null;
        try {
            role = commandEvent.getGuild().getRoleById(commandEvent.getArgs());
        } catch (NumberFormatException ignored) {}

        if (role == null) {
            var rolesByName = commandEvent.getGuild().getRolesByName(commandEvent.getArgs(), true);
            if (rolesByName.size() > 1) {
                commandEvent.replyWarning("More than 1 role with that name found, please use the ID to select role.");
                return;
            } else if (rolesByName.size() == 0) {
                commandEvent.replyWarning("Role not found, please try again with the ID or name.");
                return;
            }
            role = rolesByName.get(0);
        }

        GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
        guildService.setRecruitRole(guild, role.getId());
        commandEvent.reactSuccess();
    }
}
