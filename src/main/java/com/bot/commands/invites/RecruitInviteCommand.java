package com.bot.commands.invites;

import com.bot.commands.RequiredArgsCommand;
import com.bot.service.GuildService;
import com.bot.service.InviteService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RecruitInviteCommand extends Command {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private GuildService guildService;

    public RecruitInviteCommand() {
        this.name = "recruitinvite";
        this.aliases = new String[] {"rinvite"};
        this.arguments = "Channel reference to where the invite should go";
        this.help = "Generates a new 1 use invite to send to a new recruit";
        this.botPermissions = new Permission[] {Permission.CREATE_INSTANT_INVITE};
        this.userPermissions = new Permission[] {Permission.CREATE_INSTANT_INVITE};
        this.cooldownScope = CooldownScope.GUILD;
        this.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        var guild = guildService.getById(commandEvent.getGuild().getId());

        var channels = commandEvent.getMessage().getMentionedChannels();
        if (channels.size() == 0) {
            if (!guild.hasDefaultInviteChannel()) {
                commandEvent.replyWarning("You need to mention a channel or set a rules channel for the guild to send the " +
                        "invited user to.");
                return;
            }
            // Fill channels with the defualt rules channel
            channels = Collections.singletonList(commandEvent.getGuild().getTextChannelById(guild.getEntryChannel()));
        }

        var channel = channels.get(0);
        if (channel == null) {
            commandEvent.replyWarning("Channel not found, please check that the specified or rules channel are correct.");
            return;
        }
        List<String> roles = guild.hasDefaultRecruitRole() ?
                Collections.singletonList(guild.getRecruitRole())
                : Collections.emptyList();
        var invite = channel.createInvite().setUnique(true).setMaxAge(12L, TimeUnit.HOURS).setMaxUses(1).complete();
        inviteService.add(commandEvent.getGuild(), invite, roles, commandEvent.getAuthor().getId());
        commandEvent.replySuccess("Generated invite: " + invite.getUrl());
    }
}
