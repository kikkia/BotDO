package com.bot.commands.invites;

import com.bot.commands.RequiredArgsCommand;
import com.bot.models.InviteArgumentTag;
import com.bot.service.GuildService;
import com.bot.service.InviteService;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CreateInviteCommand extends RequiredArgsCommand {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private GuildService guildService;

    public CreateInviteCommand(InviteService inviteService) {
        this.name = "createinvite";
        this.help = "Creates an invite for inviting external users";
        this.aliases = new String[] {"invite", "cinvite"};
        this.botPermissions = new Permission[] {Permission.CREATE_INSTANT_INVITE};
        this.userPermissions = new Permission[] {Permission.CREATE_INSTANT_INVITE};
        this.cooldownScope = CooldownScope.GUILD;
        this.cooldown = 5;
    }

    @Override
    protected void executeCommand(CommandEvent commandEvent) {
        var guild = guildService.getById(commandEvent.getGuild().getId());
        var args = commandEvent.getArgs();
        if (commandEvent.getMessage().getMentionedChannels().size() == 0
                && guild.getEntryChannel() == null) {
            commandEvent.replyWarning("No channel specified, please specify a channel with the -c arg or set a default " +
                    "with the `setruleschannel` command.");
            return;
        }
        var channel = commandEvent.getMessage().getMentionedChannels().size() != 0
                ? commandEvent.getMessage().getMentionedChannels().get(0)
                : commandEvent.getGuild().getTextChannelById(guild.getEntryChannel());

        if (channel == null) {
            commandEvent.replyWarning("Channel not found, please check that the specified or rules channel are correct.");
            return;
        }

        var roles = InviteArgumentTag.Companion.getRoleNames(args);
        var welcome = InviteArgumentTag.Companion.getWelcomeMessage(args);
        var guildName = InviteArgumentTag.Companion.getGuildPrefix(args);
        var maxUses = InviteArgumentTag.Companion.getUses(args);
        var recruit = InviteArgumentTag.Companion.getRecruit(args);

        List<String> roleIds = new ArrayList<>();
        for (String name : roles) {
            var role = commandEvent.getGuild().getRolesByName(name, true);
            if (role.size() == 0) {
                commandEvent.replyWarning("Role not found: " + name + ". Please try again.");
                return;
            }
            roleIds.add(role.get(0).getId());
        }

        Invite invite;
        try {
            invite = channel.createInvite().setUnique(true)
                    .setMaxUses(maxUses).complete();
        } catch (InsufficientPermissionException e) {
            commandEvent.replyWarning("Please make sure I have permission to make create an invite in " + channel.getAsMention());
            return;
        }

        inviteService.add(commandEvent.getGuild(), invite, roleIds, welcome, guildName, commandEvent.getAuthor().getId(), recruit);
        commandEvent.replySuccess("Generated Invite: " + invite.getUrl());
    }
}
