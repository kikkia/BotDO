package com.bot.commands.server;

import com.bot.service.GuildService;
import com.bot.service.UserService;
import com.bot.tasks.SyncUserFamilyNameTask;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

@Component
public class SyncFamilyNamesCommand extends Command {

    @Autowired
    private GuildService guildService;
    @Autowired
    private UserService userService;
    @Autowired
    private ScheduledExecutorService executorService;

    public SyncFamilyNamesCommand() {
        this.name = "syncfamily";
        this.aliases = new String[]{"syncfamilynames", "syncfamilyname"};
        this.arguments = "<once | on | off>";
        this.help = "Enables/Disables/runs once syncing family names to discord nicknames. " +
                "When enabled i'll append family names to users discord names immediately, on name change, " +
                "and family name change.";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[] {Permission.NICKNAME_MANAGE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify an option for this command: " + this.arguments);
            return;
        }

        var guild = guildService.getById(commandEvent.getGuild().getId());
        if (commandEvent.getArgs().equalsIgnoreCase("on")) {
            if (!guild.getSyncNames()) {
                guildService.setSyncNames(guild, true);
                // Send off async task to rename users
                executorService.submit(new SyncUserFamilyNameTask(commandEvent.getGuild(), userService));
            }
        } else if (commandEvent.getArgs().equalsIgnoreCase("off")) {
            guildService.setSyncNames(guild, false);
        } else if (commandEvent.getArgs().equalsIgnoreCase("once")) {
            // Sync names once but not persistently
            executorService.submit(new SyncUserFamilyNameTask(commandEvent.getGuild(), userService));
        } else {
            commandEvent.replyWarning("Unrecognized input, please only use `on` or `off`.");
            return;
        }
        commandEvent.reactSuccess();
    }
}
