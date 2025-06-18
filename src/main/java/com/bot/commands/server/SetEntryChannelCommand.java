package com.bot.commands.server;

import com.bot.commands.RequiredArgsCommand;
import com.bot.db.entities.GuildEntity;
import com.bot.service.GuildService;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetEntryChannelCommand extends RequiredArgsCommand {

    @Autowired
    private GuildService guildService;

    public SetEntryChannelCommand() {
        this.name = "setentrychannel";
        this.help = "Sets the default channel for new invites to the guild.";
        this.arguments = "Link to the channel";
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void executeCommand(CommandEvent commandEvent) {
        if (commandEvent.getMessage().getMentions().getChannels().isEmpty()) {
            commandEvent.replyWarning("You need to specify a channel to use for invite links.");
            return;
        }
        GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
        guildService.setEntryChannel(guild,
                commandEvent.getMessage().getMentions().getChannels().get(0).getId());
        commandEvent.reactSuccess();
    }
}
