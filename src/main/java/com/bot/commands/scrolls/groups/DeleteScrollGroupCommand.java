package com.bot.commands.scrolls.groups;

import com.bot.db.entities.ScrollGroup;
import com.bot.service.ScrollGroupService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DeleteScrollGroupCommand extends Command {

    @Autowired
    private ScrollGroupService scrollGroupService;

    public DeleteScrollGroupCommand() {
        this.name = "deletescrollgroup";
        this.arguments = "<group name>";
        this.help = "Removes a given scroll group";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify a group name to delete.");
            return;
        }

        Optional<ScrollGroup> existingGroup = scrollGroupService.getByGuildIdAndName(commandEvent.getGuild().getId(), commandEvent.getArgs());
        if (existingGroup.isEmpty()) {
            commandEvent.replyWarning("I could not find a group for that given name.");
            return;
        }
        scrollGroupService.removeById(existingGroup.get().getId());
        commandEvent.reactSuccess();
    }
}
