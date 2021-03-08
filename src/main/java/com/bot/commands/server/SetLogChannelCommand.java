package com.bot.commands.server;

import com.bot.commands.RequiredArgsCommand;
import com.bot.db.entities.GuildEntity;
import com.bot.service.GuildService;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetLogChannelCommand extends RequiredArgsCommand {

    @Autowired
    private GuildService guildService;

    public SetLogChannelCommand() {
        this.name = "setlogchannel";
        this.help = "Sets the default channel to log bot information to. (Error info or other relevant logs)";
        this.arguments = "Link to the channel";
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void executeCommand(CommandEvent commandEvent) {
        if (commandEvent.getMessage().getMentionedChannels().size() < 1) {
            commandEvent.replyWarning("You need to link a channel to use for logs.");
            return;
        }
        GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
        guildService.setLogChannel(guild,
                commandEvent.getMessage().getMentionedChannels().get(0).getId());
        commandEvent.reactSuccess();
    }
}
