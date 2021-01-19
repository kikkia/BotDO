package com.bot;

import com.bot.db.entities.Guild;
import com.bot.db.entities.TextChannel;
import com.bot.db.entities.User;
import com.bot.service.GuildService;
import com.bot.service.TextChannelService;
import com.bot.service.UserService;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class DiscordListener extends ListenerAdapter {

    @Autowired
    private GuildService guildService;
    @Autowired
    private UserService userService;
    @Autowired
    private TextChannelService textChannelService;

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // Add users to guild and user tables
        guildService.addFreshGuild(event.getGuild());
        super.onGuildJoin(event);
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        var guild = guildService.getById(event.getGuild().getId());
        if (guild == null) {
            guild = guildService.addFreshGuild(event.getGuild());
        }

        User user = userService.getById(event.getUser().getId());
        if(user == null) {
            user = userService.addUser(event.getUser().getId(),
                    event.getUser().getName());
        }
        guildService.addUser(guild, user);

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

        super.onGuildMemberRemove(event);
    }

    @Override
    public void onTextChannelDelete(@Nonnull TextChannelDeleteEvent event) {
        textChannelService.removeById(event.getChannel().getId());

        super.onTextChannelDelete(event);
    }

    @Override
    public void onTextChannelUpdateName(@Nonnull TextChannelUpdateNameEvent event) {
        TextChannel channel = textChannelService.getById(event.getChannel().getId());
        Guild guild = guildService.getById(event.getGuild().getId());
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
        Guild guild = guildService.getById(event.getGuild().getId());
        if (guild == null) {
            guildService.addFreshGuild(event.getGuild());
            return;
        }

        textChannelService.add(event.getChannel(), guild);

        super.onTextChannelCreate(event);
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        // Cleanup all stuff

        super.onGuildLeave(event);
    }

    @Override
    public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event) {
        Guild guild = guildService.getById(event.getGuild().getId());
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
}
