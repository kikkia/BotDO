package com.bot.commands.scrolls;

import com.bot.db.entities.ScrollGroup;
import com.bot.service.ScrollGroupService;
import com.bot.service.UserService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JoinGroupCommand extends Command {

    @Autowired
    private ScrollGroupService groupService;
    @Autowired
    private UserService userService;

    public JoinGroupCommand() {
        this.name = "joingroup";
        this.arguments = "<group name>";
        this.help = "Joins you to a given scroll group";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify a group name to join");
            return;
        }

        Optional<ScrollGroup> existingGroup = groupService.getByGuildIdAndName(commandEvent.getGuild().getId(), commandEvent.getArgs());
        if (existingGroup.isEmpty()) {
            commandEvent.replyWarning("I could not find a group for that given name.");
            return;
        }

        var group = existingGroup.get();
        var users = group.getUsers();
        users.add(userService.getById(commandEvent.getAuthor().getId()));
        group.setUsers(users);
        groupService.save(group);
        commandEvent.reactSuccess();
    }
}
