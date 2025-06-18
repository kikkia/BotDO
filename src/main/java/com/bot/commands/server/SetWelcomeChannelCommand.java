package com.bot.commands.server;

import com.bot.commands.RequiredArgsCommand;
import com.bot.db.entities.GuildEntity;
import com.bot.service.GuildService;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetWelcomeChannelCommand extends RequiredArgsCommand {

    @Autowired
    private GuildService guildService;

    public SetWelcomeChannelCommand() {
        this.name = "setwelcomechannel";
        this.help = "Sets the channel to put welcome messages in.";
        this.arguments = "Link to channel";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    protected void executeCommand(CommandEvent commandEvent) {
        if (commandEvent.getMessage().getMentions().getChannels().isEmpty()) {
            commandEvent.replyWarning("You need to specify a channel to use for welcome messages.");
            return;
        }
        GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
        guildService.setWelcomeChannel(guild,
                commandEvent.getMessage().getMentions().getChannels().get(0).getId());
        commandEvent.reactSuccess();
    }
}
