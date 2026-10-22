package com.bot.commands.user;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import com.bot.db.entities.UserEntity;
import com.bot.listeners.CommandListener;
import com.bot.models.Region;
import com.bot.service.FamilyService;
import com.bot.service.MetricsService;
import com.bot.service.UserService;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestLookupCommands {

    private final FamilyService familyService = mock(FamilyService.class);
    private final UserService userService = mock(UserService.class);
    private final MetricsService metricsService = mock(MetricsService.class);
    private final CommandListener listener = new CommandListener(metricsService);
    private final SlashCommandEvent event = mock(SlashCommandEvent.class);
    private final User discordUser = mock(User.class);
    private final ReplyCallbackAction deferred = mock(ReplyCallbackAction.class);
    private final InteractionHook hook = mock(InteractionHook.class);
    private final WhoIsCommand whois = new WhoIsCommand(familyService, userService, listener);
    private final SetDefaultRegionCommand defaultRegion = new SetDefaultRegionCommand(userService, listener);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        WebhookMessageEditAction<Message> edit = mock(WebhookMessageEditAction.class);
        when(event.getUser()).thenReturn(discordUser);
        when(discordUser.getId()).thenReturn("123");
        when(discordUser.getName()).thenReturn("kikkia");
        when(event.deferReply()).thenReturn(deferred);
        when(event.deferReply(true)).thenReturn(deferred);
        when(event.isAcknowledged()).thenReturn(true);
        when(event.getHook()).thenReturn(hook);
        when(hook.editOriginal(anyString())).thenReturn(edit);
        when(hook.editOriginalEmbeds(any(MessageEmbed.class))).thenReturn(edit);
        option("family", "kikkia");
    }

    @Test
    void lookupDefersBeforeAccessingTheDatabase() {
        whois.execute(event);

        verify(event).deferReply();
        verifyNoInteractions(userService, familyService);
    }

    @Test
    void firstTimeUserCanLookUpHistoryWithoutGuildMembershipOrAStoredUser() {
        FamilyEntity family = family("NA", false);
        family.setMemberships(List.of(membership(family, "CurrentGuild", true), membership(family, "PastGuild", false)));
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenReturn(Optional.of(family));

        whois.execute(event);
        acknowledge();

        MessageEmbed embed = resultEmbed();
        assertEquals("kikkia", embed.getTitle());
        assertEquals("CurrentGuild", embed.getFields().get(0).getValue());
        assertEquals("PastGuild", embed.getFields().get(1).getValue());
        assertTrue(embed.getFooter().getText().contains("NA"));
        verify(event, never()).getMember();
        verify(userService, never()).save(any());
    }

    @Test
    void lookupUsesTheSavedDefaultRegion() {
        UserEntity user = new UserEntity("123", "kikkia");
        user.setDefaultRegion("EU");
        when(userService.getById("123")).thenReturn(user);
        when(familyService.getFamily("kikkia", Region.EUROPE, true)).thenReturn(Optional.of(family("EU", false)));

        whois.execute(event);
        acknowledge();

        verify(familyService).getFamily("kikkia", Region.EUROPE, true);
        assertTrue(resultEmbed().getFooter().getText().contains("EU"));
    }

    @Test
    void explicitRegionOverridesTheDefaultAndTrimsTheFamilyName() {
        option("family", "  kikkia  ");
        option("region", "EU");
        when(familyService.getFamily("kikkia", Region.EUROPE, true)).thenReturn(Optional.of(family("EU", false)));

        whois.execute(event);
        acknowledge();

        verify(familyService).getFamily("kikkia", Region.EUROPE, true);
        verifyNoInteractions(userService);
        resultEmbed();
    }

    @Test
    void missingFamilyReturnsAnActionableMessage() {
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenReturn(Optional.empty());

        whois.execute(event);
        acknowledge();

        verify(hook).editOriginal(contains("Check the family name and region"));
    }

    @Test
    void privateFamilyHasAUsefulCurrentGuildLabel() {
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenReturn(Optional.of(family("NA", true)));

        whois.execute(event);
        acknowledge();

        MessageEmbed embed = resultEmbed();
        assertEquals("Private", embed.getFields().get(0).getValue());
        assertEquals("None that I know of.", embed.getFields().get(1).getValue());
    }

    @Test
    void staleCachedHistoryIsStillShownWithAWarning() {
        FamilyEntity family = family("NA", false);
        family.setLastUpdated(Timestamp.from(Instant.now().minus(2, ChronoUnit.DAYS)));
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenReturn(Optional.of(family));

        whois.execute(event);
        acknowledge();

        assertTrue(resultEmbed().getDescription().contains("could not be refreshed"));
        verify(familyService, never()).syncSingleFromSite(anyString(), any());
    }

    @Test
    void longHistoryFitsWithinDiscordEmbedLimits() {
        FamilyEntity family = family("NA", false);
        List<GuildMembershipEntity> memberships = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            memberships.add(membership(family, "PastGuild" + i, false));
        }
        family.setMemberships(memberships);
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenReturn(Optional.of(family));

        whois.execute(event);
        acknowledge();

        String history = resultEmbed().getFields().get(1).getValue();
        assertEquals(MessageEmbed.VALUE_MAX_LENGTH, history.length());
        assertTrue(history.endsWith("..."));
    }

    @Test
    void blankFamilyNameDoesNotQueryTheDatabase() {
        option("family", "   ");

        whois.execute(event);
        acknowledge();

        verify(hook).editOriginal("Please provide a BDO family name.");
        verifyNoInteractions(userService, familyService);
    }

    @Test
    void invalidStoredRegionDoesNotCauseANullPointerException() {
        UserEntity user = new UserEntity("123", "kikkia");
        user.setDefaultRegion("invalid");
        when(userService.getById("123")).thenReturn(user);

        whois.execute(event);
        acknowledge();

        verify(hook).editOriginal(contains("/defaultregion"));
        verifyNoInteractions(familyService);
    }

    @Test
    void lookupFailureReplacesTheLoadingMessageAndRecordsTheFailure() {
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenThrow(new IllegalStateException("Database unavailable"));

        whois.execute(event);
        acknowledge();

        verify(metricsService).markCommandFailure("whois");
        verify(hook).editOriginal(contains("DM kikkia"));
    }

    @Test
    void defaultRegionCreatesAUserWithoutProvisioningRetiredFeatures() {
        option("region", "eu");

        defaultRegion.execute(event);
        verifyNoInteractions(userService);
        acknowledge();

        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService).save(saved.capture());
        assertEquals("123", saved.getValue().getId());
        assertEquals("kikkia", saved.getValue().getName());
        assertEquals("EU", saved.getValue().getDefaultRegion());
        assertNull(saved.getValue().getInventory());
        verify(event).deferReply(true);
        verify(event, never()).getMember();
        verify(hook).editOriginal("Your default lookup region is now EU.");
    }

    @Test
    void defaultRegionPreservesExistingUserData() {
        UserEntity user = new UserEntity("123", "kikkia");
        user.setFamilyName("ExistingFamily");
        when(userService.getById("123")).thenReturn(user);
        option("region", "EU");

        defaultRegion.execute(event);
        acknowledge();

        verify(userService).save(same(user));
        assertEquals("ExistingFamily", user.getFamilyName());
        assertEquals("EU", user.getDefaultRegion());
    }

    @Test
    void invalidDefaultRegionDoesNotSaveAnything() {
        option("region", "invalid");

        defaultRegion.execute(event);
        acknowledge();

        verify(hook).editOriginal(contains("Unknown region"));
        verifyNoInteractions(userService);
    }

    @Test
    void prefixWhoisStillUsesTheLookupWhenPrefixCommandsAreEnabled() {
        CommandEvent prefixEvent = mock(CommandEvent.class);
        when(prefixEvent.getAuthor()).thenReturn(discordUser);
        when(prefixEvent.getArgs()).thenReturn("  kikkia  ");
        when(familyService.getFamily("kikkia", Region.NORTH_AMERICA, true)).thenReturn(Optional.of(family("NA", false)));

        whois.execute(prefixEvent);

        verify(prefixEvent).reply(any(MessageEmbed.class));
        verify(prefixEvent, never()).getMember();
    }

    @Test
    void prefixDefaultRegionStillSavesThePreferenceWhenPrefixCommandsAreEnabled() {
        CommandEvent prefixEvent = mock(CommandEvent.class);
        when(prefixEvent.getAuthor()).thenReturn(discordUser);
        when(prefixEvent.getArgs()).thenReturn(" eu ");

        defaultRegion.execute(prefixEvent);

        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService).save(saved.capture());
        assertEquals("EU", saved.getValue().getDefaultRegion());
        verify(prefixEvent).replySuccess("Your default lookup region is now EU.");
        verify(prefixEvent, never()).getMember();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void acknowledge() {
        ArgumentCaptor<Consumer<InteractionHook>> callback = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(deferred).queue(callback.capture());
        callback.getValue().accept(hook);
    }

    private void option(String name, String value) {
        OptionMapping option = mock(OptionMapping.class);
        when(option.getAsString()).thenReturn(value);
        when(event.getOption(name)).thenReturn(option);
    }

    private MessageEmbed resultEmbed() {
        ArgumentCaptor<MessageEmbed> embed = ArgumentCaptor.forClass(MessageEmbed.class);
        verify(hook).editOriginalEmbeds(embed.capture());
        return embed.getValue();
    }

    private FamilyEntity family(String region, boolean isPrivate) {
        return new FamilyEntity(1, "external", List.of(), "kikkia", region, Timestamp.from(Instant.now()), isPrivate, 0, false);
    }

    private GuildMembershipEntity membership(FamilyEntity family, String guildName, boolean active) {
        BDOGuildEntity guild = new BDOGuildEntity(1, guildName, family.getRegion(), Timestamp.from(Instant.now()));
        return new GuildMembershipEntity(1, family, guild, Timestamp.from(Instant.now()), active);
    }
}
