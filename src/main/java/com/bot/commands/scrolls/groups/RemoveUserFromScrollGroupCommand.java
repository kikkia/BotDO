package com.bot.commands.scrolls.groups;

import com.bot.db.entities.ScrollGroup;
import com.bot.service.ScrollGroupService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RemoveUserFromScrollGroupCommand extends Command {

    @Autowired
    private ScrollGroupService groupService;

    public RemoveUserFromScrollGroupCommand() {
        this.name = "removeuser";
        this.arguments = "<group_name family_name>";
        this.help = "removes a user by family name from a group";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank() || commandEvent.getArgs().split(" ").length != 2) {
            commandEvent.replyWarning("You need to specify a group name and family name, separated by a space.");
            return;
        }
        var args = commandEvent.getArgs().split(" ");

        Optional<ScrollGroup> existingGroup = groupService.getByGuildIdAndName(commandEvent.getGuild().getId(), args[0]);
        if (existingGroup.isEmpty()) {
            commandEvent.replyWarning("I could not find a group for that given name.");
            return;
        }

        var group = existingGroup.get();
        var cleanedUsers = group.getUsers().stream()
                .filter(it -> !it.getFamilyName().equalsIgnoreCase(args[1]))
                .collect(Collectors.toSet());

        if (group.getUsers().size() == cleanedUsers.size()) {
            commandEvent.replyWarning("Could not find any users with that family name in the group.");
            return;
        }

        group.setUsers(cleanedUsers);
        groupService.save(group);
        commandEvent.reactSuccess();
    }
}
