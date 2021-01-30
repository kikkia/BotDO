package com.bot.commands.scrolls.groups;

import com.bot.db.entities.ScrollGroup;
import com.bot.service.ScrollGroupService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddScrollGroupCommand extends Command {

    @Autowired
    private ScrollGroupService groupService;

    public AddScrollGroupCommand() {
        this.name = "addscrollgroup";
        this.aliases = new String[] {"createscrollgroup", "addsg"};
        this.arguments = "<name of new group>";
        this.help = "Creates a new scroll group";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify a name for this scroll group");
            return;
        }

        Optional<ScrollGroup> existingGroup = groupService.getByGuildIdAndName(commandEvent.getGuild().getId(), commandEvent.getArgs());
        if (existingGroup.isPresent()) {
            commandEvent.replyWarning("A group in this guild with that name already exists, please choose a new name");
            return;
        }
        var group = groupService.create(commandEvent.getGuild(), commandEvent.getArgs());
        commandEvent.replySuccess("Created group: " + group.getName());
    }
}
