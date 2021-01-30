package com.bot.commands.user;

import com.bot.service.UserService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChangeFamilyNameCommand extends Command {

    @Autowired
    private UserService userService;

    public ChangeFamilyNameCommand() {
        this.name = "changefamilyname";
        this.aliases = new String[] {"setfamilyname"};
        this.help = "Changes your family name (You want this to match your in game family name)";
        this.arguments = "<family name>";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify a family name to set");
            return;
        }

        userService.setFamilyName(commandEvent.getAuthor().getId(), commandEvent.getArgs());
        commandEvent.reactSuccess();
    }
}
