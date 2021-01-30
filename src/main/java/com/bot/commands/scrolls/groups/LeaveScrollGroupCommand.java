package com.bot.commands.scrolls.groups;

import com.bot.db.entities.ScrollGroup;
import com.bot.service.ScrollGroupService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LeaveScrollGroupCommand extends Command {

    @Autowired
    private ScrollGroupService groupService;

    public LeaveScrollGroupCommand() {
        this.name = "leavegroup";
        this.arguments = "<group name>";
        this.help = "Leaves a given scroll group";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify a group name to leave.");
            return;
        }

        Optional<ScrollGroup> existingGroup = groupService.getByGuildIdAndName(commandEvent.getGuild().getId(), commandEvent.getArgs());
        if (existingGroup.isEmpty()) {
            commandEvent.replyWarning("I could not find a group for that given name.");
            return;
        }

        var group = existingGroup.get();
        var cleanedUsers = group.getUsers().stream()
                .filter(it -> !it.getId().equals(commandEvent.getAuthor().getId()))
                .collect(Collectors.toSet());

        if (group.getUsers().size() == cleanedUsers.size()) {
            commandEvent.replyWarning("You are not in that group.");
            return;
        }

        group.setUsers(cleanedUsers);
        groupService.save(group);
        commandEvent.reactSuccess();
    }
}
