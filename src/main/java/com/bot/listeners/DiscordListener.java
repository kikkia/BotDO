package com.bot.listeners;

import com.bot.configuration.properties.DiscordProperties;
import com.bot.db.entities.*;
import com.bot.models.Region;
import com.bot.service.*;
import com.bot.tasks.InvitedMemberTask;
import com.bot.tasks.ScanGuildsTask;
import com.bot.tasks.SyncUserFamilyNameTask;
import com.bot.utils.Constants;
import com.bot.utils.GuildScrapeUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DiscordListener extends ListenerAdapter {

    @Autowired
    private GuildService guildService;
    @Autowired
    private UserService userService;
    @Autowired
    private TextChannelService textChannelService;
    @Autowired
    private VoiceChannelService voiceChannelService;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private BdoGuildService bdoGuildService;
    @Autowired
    private WarService warService;
    @Autowired
    private WarVodService warVodService;
    @Autowired
    private WarStatsService warStatsService;
    @Autowired
    private ScheduledExecutorService executorService;
    @Autowired
    private DiscordProperties discordProperties;
    @Autowired
    private MetricsService metricsService;
    @Autowired
    private GuildScrapeUtils guildScrapeUtils;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        // Schedule the NA scan
        // TODO: This runs once for every shard, be careful if sharding is ever needed
        if (discordProperties.getScanFamilies()) {
                executorService.scheduleAtFixedRate(new ScanGuildsTask(familyService, bdoGuildService, metricsService, guildScrapeUtils, Region.NORTH_AMERICA),
                        12, 24, TimeUnit.HOURS);
                executorService.scheduleAtFixedRate(new ScanGuildsTask(familyService, bdoGuildService, metricsService, guildScrapeUtils, Region.EUROPE),
                        0, 24, TimeUnit.HOURS);
        }

        // Open dm channels with users with active dm signups to listen for updates
        List<String> usersWithDmSignups = warService
                .getActiveDmSignups()
                .stream()
                .map(WarDmSignupEntity::getUserId)
                .collect(Collectors.toList());
        for (String userId: usersWithDmSignups) {
            var user = event.getJDA().getUserById(userId);
            if (user != null) {
                // Cached if already opened
                user.openPrivateChannel().queue();
            }
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // Add users to guild and user tables
        guildService.addFreshGuild(event.getGuild());
        super.onGuildJoin(event);
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        log.info("Member joined: " + event.getMember().getEffectiveName());
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
                        event.getUser().getName(), event.getUser().getAvatarUrl());
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
        TextChannelEntity channel = textChannelService.getById(event.getChannel().getId());
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
        if (event.getUser().isBot()) {
            return;
        }
        // TODO: For use in reaction roles
        // Check if this message is a war attendance message
        Optional<WarEntity> warOpt = warService.getWarByMessageId(event.getMessageId());
        warOpt.ifPresent(warEntity -> handleWarReaction(warEntity, event));
        super.onGuildMessageReactionAdd(event);
    }

    @Override
    public void onPrivateMessageReactionAdd(@NotNull PrivateMessageReactionAddEvent event) {
        if (!event.getUser().isBot()) {
            Optional<WarEntity> warOpt = warService.getWarByDmMessageId(event.getMessageId());
            warOpt.ifPresent(warEntity -> handleDmWarReaction(warEntity, event));
            super.onPrivateMessageReactionAdd(event);
        }
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

    @Override
    public void onGuildUpdateIcon(@NotNull GuildUpdateIconEvent event) {
        super.onGuildUpdateIcon(event);
    }

    @Override
    public void onUserUpdateAvatar(@NotNull UserUpdateAvatarEvent event) {
        super.onUserUpdateAvatar(event);
    }

    @Override
    public void onVoiceChannelUpdateName(@NotNull VoiceChannelUpdateNameEvent event) {
        var channel = getVoiceHelper(event.getChannel());
        channel.setName(event.getNewName());
        voiceChannelService.save(channel);

        super.onVoiceChannelUpdateName(event);
    }

    @Override
    public void onVoiceChannelDelete(@NotNull VoiceChannelDeleteEvent event) {
        voiceChannelService.delete(event.getChannel().getId());
        super.onVoiceChannelDelete(event);
    }

    @Override
    public void onVoiceChannelCreate(@NotNull VoiceChannelCreateEvent event) {
        voiceChannelService.newChannel(event.getChannel().getId(),
                event.getChannel().getName(),
                event.getGuild().getId());
        super.onVoiceChannelCreate(event);
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        handleVoiceChannelChange(event.getChannelJoined(), event.getMember());
        super.onGuildVoiceMove(event);
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        handleVoiceChannelChange(event.getChannelJoined(), event.getMember());
        super.onGuildVoiceJoin(event);
    }

    private void handleVoiceChannelChange(VoiceChannel channel, Member member) {
        var channelEntity = getVoiceHelper(channel);
        if (channelEntity.getWar()) {
            var guild = guildService.getById(channel.getGuild().getId());
            var warOpt = warService.getByGuildInNextHour(guild);
            if (warOpt.isPresent()) {
                // Check that user is signed up for the war
                var war = warOpt.get();
                var attendanceRecordOpt = war.getAttendees()
                        .stream()
                        .filter(a -> a.getUser().getId().equals(member.getUser().getId()))
                        .findFirst();
                if (attendanceRecordOpt.isEmpty() ||
                        attendanceRecordOpt.get().getNotAttending() ||
                        attendanceRecordOpt.get().getMaybe()) {
                    // Send dm to them to sign up
                    var privateChannel = member
                            .getUser()
                            .openPrivateChannel()
                            .complete();
                    privateChannel.sendMessage("I noticed you just joined a war voice channel for a war you haven't yet signed up for." +
                            " We really want to encourage people to sign up for war in the bot so we can track numbers and make sure everyone that" +
                            " wants to war can get a spot. If you are participating please yes up in the bot by reacting in the signup channel `" +
                            war.getChannel().getName() + "` before you yes up in game. If not you can ignore this dm. Thanks!").queue();
                }
                if (attendanceRecordOpt.isPresent()) {
                    // Set them as attending the war
                    warService.setAttendeeAttended(war, member.getUser().getId());
                }
            }
        }
    }

    // Get the voice channel entity or create it if does not exist
    private VoiceChannelEntity getVoiceHelper(VoiceChannel voiceChannel) {
        var channelOpt = voiceChannelService.getById(voiceChannel.getId());
        return channelOpt.orElseGet(() -> voiceChannelService.newChannel(
                voiceChannel.getId(),
                voiceChannel.getName(),
                voiceChannel.getGuild().getId()));
    }

    private void handleWarReaction(WarEntity warEntity, GuildMessageReactionAddEvent event) {
        warEntity = handleWarAttendanceUpdate(warEntity, event.getUserId(), event.getReactionEmote().getName());
        warService.refreshMessage(event.getGuild(), warEntity);
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    private WarEntity handleWarAttendanceUpdate(WarEntity warEntity, String userId, String reactionName) {
        // TODO: Family name in guild verification
        // TODO: Move some logic to service layer
        // If emoji is not a proper emoji, remove it
        var user = userService.getById(userId);

        if (Constants.WAR_REACTIONS.contains(reactionName)) {
            // If war has happened already, only refresh
            if (warEntity.getArchived()) {
                // Do nothing just refresh
            } else if (reactionName.equals(Constants.WAR_REACTION_YES)) {
                var attended = false; // Used to maintain attended status
                // If they are signed up as maybe, remove the maybe
                var attendeeOpt = warEntity.getAttendees().stream()
                        .filter(a -> a.getUser().getId().equals(userId)).findFirst();
                if (attendeeOpt.isPresent()) {
                    attended = attendeeOpt.get().getAttended();
                    // Remove and re add attendee as maybe (We want to refresh their created timestamp)
                    warEntity = warService.removeAttendee(warEntity, user);
                }

                // If they are not signed up then add them
                if (!warEntity.getAttendees().stream().map(a -> a.getUser().getId()).collect(Collectors.toList()).contains(userId)) {
                    warEntity = warService.addAttendee(warEntity, user, false);
                    if (attended)
                        warService.setAttendeeAttended(warEntity, user.getId());
                }
            } else if (reactionName.equals(Constants.WAR_REACTION_NO)) {
                // If no reaction remove them from the list if they are on it
                warEntity = warService.removeAttendee(warEntity, user);
                // Add a new entity with the NO
                warEntity = warService.notAttending(warEntity, user);
            } else if (reactionName.equals(Constants.WAR_REACTION_MAYBE)) {
                // Set the user to maybe or create maybe attendance
                var attendeeOpt = warEntity.getAttendees().stream().filter(a -> a.getUser().getId().equals(userId)).findFirst();
                if (attendeeOpt.isPresent()) {
                    // Remove and re add attendee as maybe (We want to refresh their created timestamp)
                    warEntity = warService.removeAttendee(warEntity, userService.getById(userId));
                }
                warEntity = warService.addAttendee(warEntity, user, true);
            }
            // Deactivate their dm signups (Cleans up the need for opening dm channel with user on startup)
            warService.deactivateDmSignup(warEntity, userId);
        }
        return warEntity;
    }

    private void handleDmWarReaction(WarEntity warEntity, PrivateMessageReactionAddEvent event) {
        warEntity = handleWarAttendanceUpdate(warEntity, event.getUserId(), event.getReactionEmote().getName());
        Guild guild = event.getJDA().getGuildById(warEntity.getGuild().getDiscordGuild().getId());
        if (guild != null) {
            warService.refreshMessage(guild, warEntity);
        }
        event.getChannel().sendMessage("Thank you for responding, if you want to change your answer please do it in the "
                + warEntity.getChannel().getName() + " channel.").queue();
    }
}
