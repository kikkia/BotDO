package com.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class RequiredArgsCommand extends Command {

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (checkArgs(commandEvent)) {
            try {
                executeCommand(commandEvent);
            } catch(Exception e) {
                commandEvent.replyError("Something went wrong executing the command. Please try again and if this issue " +
                        "continues please reach out for support.");
            }
        }
    }

    private boolean checkArgs(CommandEvent event) {
        if (event.getArgs().isBlank()) {
            event.replyWarning("Arguments are required for this command: " + this.arguments);
            return false;
        }
        return true;
    }

    protected abstract void executeCommand(CommandEvent commandEvent);
}
