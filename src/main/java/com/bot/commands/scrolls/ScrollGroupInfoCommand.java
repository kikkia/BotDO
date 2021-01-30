package com.bot.commands.scrolls;

import com.bot.db.entities.ScrollGroup;
import com.bot.service.ScrollGroupService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScrollGroupInfoCommand extends Command {

    @Autowired
    private ScrollGroupService groupService;

    public ScrollGroupInfoCommand() {
        this.name = "scrollgroup";
        this.aliases = new String[] {"groupinfo", "scrollgroupinfo"};
        this.arguments = "<name of the group>";
        this.help = "Gets information about a given scroll group";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify a name to lookup");
            return;
        }

        Optional<ScrollGroup> existingGroup = groupService.getByGuildIdAndName(commandEvent.getGuild().getId(), commandEvent.getArgs());
        if (existingGroup.isEmpty()) {
            commandEvent.replyWarning("I could not find a group for that given name.");
            return;
        }

        var group = existingGroup.get();
        commandEvent.reply(group.toMessage());
    }
}
