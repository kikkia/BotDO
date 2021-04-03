package com.bot;

import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.TextChannel;
import com.bot.db.entities.UserEntity;
import com.bot.service.GuildService;
import com.bot.service.InviteService;
import com.bot.service.TextChannelService;
import com.bot.service.UserService;
import com.bot.tasks.InvitedMemberTask;
import com.bot.tasks.SyncUserFamilyNameTask;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class DiscordListener extends ListenerAdapter {

    @Autowired
    private GuildService guildService;
    @Autowired
    private UserService userService;
    @Autowired
    private TextChannelService textChannelService;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private ScheduledExecutorService executorService;

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // Add users to guild and user tables
        guildService.addFreshGuild(event.getGuild());
        super.onGuildJoin(event);
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        System.out.println("Member joined: " + event.getMember().getEffectiveName());
        try {
            var guild = guildService.getById(event.getGuild().getId());
            if (guild == null) {
                guild = guildService.addFreshGuild(event.getGuild());
            }

            // Check invite member joined with and act accordingly
            executorService.submit(new InvitedMemberTask(event, inviteService, userService, guild));

            UserEntity user = userService.getById(event.getUser().getId());
            if (user == null) {
                user = userService.addUser(event.getUser().getId(),
                        event.getUser().getName());
            }
            guildService.addUser(guild, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onGuildMemberJoin(event);
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        var guild = guildService.getById(event.getGuild().getId());
        if (guild == null) {
            guild = guildService.addFreshGuild(event.getGuild());
        }

        // Something is fucky with JPA, findbyID not working so this is the workaround for now
        var user = userService.getById(event.getUser().getId());
        // if we don't have them, ignore
        if (user == null) {
            return;
        }
        guildService.removeUser(guild, user);
        //test
        super.onGuildMemberRemove(event);
    }

    @Override
    public void onTextChannelDelete(@Nonnull TextChannelDeleteEvent event) {
        textChannelService.removeById(event.getChannel().getId());

        // Check for default channels removed
        var guild = guildService.getById(event.getGuild().getId());
        if (event.getChannel().getId().equals(guild.getEntryChannel())) {
            guildService.setEntryChannel(guild, null);
        }
        if (event.getChannel().getId().equals(guild.getWelcomeChannel())) {
            guildService.setWelcomeChannel(guild, null);
        }

        super.onTextChannelDelete(event);
    }

    @Override
    public void onTextChannelUpdateName(@Nonnull TextChannelUpdateNameEvent event) {
        TextChannel channel = textChannelService.getById(event.getChannel().getId());
        GuildEntity guild = guildService.getById(event.getGuild().getId());
        if (guild == null) {
            guildService.addFreshGuild(event.getGuild());
            return;
        } else if (channel == null) {
            channel = textChannelService.add(event.getChannel(), guild);
        }
        textChannelService.rename(channel, event.getNewName());
        super.onTextChannelUpdateName(event);
    }

    @Override
    public void onTextChannelCreate(@Nonnull TextChannelCreateEvent event) {
        GuildEntity guild = guildService.getById(event.getGuild().getId());
        if (guild == null) {
            guildService.addFreshGuild(event.getGuild());
            return;
        }

        textChannelService.add(event.getChannel(), guild);

        super.onTextChannelCreate(event);
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        // TODO: Cleanup all stuff

        super.onGuildLeave(event);
    }

    @Override
    public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event) {
        GuildEntity guild = guildService.getById(event.getGuild().getId());
        if (guild == null) {
            guildService.addFreshGuild(event.getGuild());
            return;
        }
        guildService.rename(guild, event.getNewName());

        super.onGuildUpdateName(event);
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        // TODO: For use in reaction roles
        super.onGuildMessageReactionAdd(event);
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        // TODO: For use in reaction roles
        super.onGuildMessageReactionRemove(event);
    }

    @Override
    public void onUserUpdateName(@NotNull UserUpdateNameEvent event) {
        userService.setUserName(event.getUser().getId(), event.getNewName());
        super.onUserUpdateName(event);
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        var guild = guildService.getById(event.getGuild().getId());
        if (guild.getSyncNames()) {
            executorService.submit(new SyncUserFamilyNameTask(event.getMember(), userService));
        }
        super.onGuildMemberUpdateNickname(event);
    }

    @Override
    public void onRoleDelete(@NotNull RoleDeleteEvent event) {
        var guild = guildService.getById(event.getGuild().getId());
        if (event.getRole().getId().equals(guild.getRecruitRole())) {
            guildService.setRecruitRole(guild, null);
        }
        super.onRoleDelete(event);
    }

    @Override
    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event) {
        inviteService.removeByCode(event.getCode());
        super.onGuildInviteDelete(event);
    }

    @Override
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        if (!event.getInvite().getInviter().getId().equals(event.getJDA().getSelfUser().getId())) {
            inviteService.addExisting(event.getInvite());
        }
        super.onGuildInviteCreate(event);

    }
}
