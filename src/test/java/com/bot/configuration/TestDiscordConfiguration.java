package com.bot.configuration;

import com.bot.commands.RetiredCommand;
import com.bot.commands.scrolls.AddScrollsCommand;
import com.bot.commands.server.SetRecruitRoleCommand;
import com.bot.commands.user.SetDefaultRegionCommand;
import com.bot.commands.user.WhoIsCommand;
import com.bot.configuration.properties.DiscordProperties;
import com.bot.listeners.CommandListener;
import com.bot.listeners.DiscordListener;
import com.bot.listeners.FamilyScanListener;
import com.bot.service.FamilyService;
import com.bot.service.MetricsService;
import com.bot.service.ScrollInventoryService;
import com.bot.service.UserService;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestDiscordConfiguration {

    @Test
    void registersOnlyLookupHandlersAndRetirementNoticesAsSlashCommands() {
        DiscordConfiguration configuration = new DiscordConfiguration();
        CommandListener listener = new CommandListener(mock(MetricsService.class));
        UserService users = mock(UserService.class);
        WhoIsCommand whois = new WhoIsCommand(mock(FamilyService.class), users, listener);
        SetDefaultRegionCommand region = new SetDefaultRegionCommand(users, listener);
        configuration.commandList = List.of(whois, region,
                new AddScrollsCommand(mock(ScrollInventoryService.class)), new SetRecruitRoleCommand());
        DiscordProperties properties = new DiscordProperties();
        properties.setOwner("123456789012345678");

        CommandClient client = configuration.commandClient(properties, mock(ScheduledExecutorService.class), listener);

        assertTrue(client.getCommands().isEmpty(), "No legacy prefix handler may be registered");
        assertEquals(Set.of("whois", "defaultregion", "addscrolls", "as", "addscroll", "setrecruitrole", "help"),
                client.getSlashCommands().stream().map(SlashCommand::getName).collect(Collectors.toSet()));
        assertTrue(client.getSlashCommands().contains(whois));
        assertTrue(client.getSlashCommands().contains(region));
        for (SlashCommand command : client.getSlashCommands()) {
            assertDoesNotThrow(command::buildCommandData);
            if (command != whois && command != region) {
                assertTrue(command instanceof RetiredCommand);
                assertEquals(0, command.getUserPermissions().length, "Old permission gates must not hide the notice");
                assertTrue(command.getOptions().isEmpty(), "The notice must not require retired arguments");
            }
        }
        assertTrue(whois.getOptions().get(0).isRequired());
        assertFalse(whois.getOptions().get(1).isRequired());
        assertTrue(region.getOptions().get(0).isRequired());
    }

    @Test
    void prefixHandlingMirrorsTheLookupsAndNoticesWithoutRestoringLegacyHandlers() {
        DiscordConfiguration configuration = new DiscordConfiguration();
        CommandListener listener = new CommandListener(mock(MetricsService.class));
        UserService users = mock(UserService.class);
        WhoIsCommand whois = new WhoIsCommand(mock(FamilyService.class), users, listener);
        SetDefaultRegionCommand region = new SetDefaultRegionCommand(users, listener);
        SetRecruitRoleCommand legacy = new SetRecruitRoleCommand();
        configuration.commandList = List.of(whois, region, legacy);
        DiscordProperties properties = new DiscordProperties();
        properties.setOwner("123456789012345678");
        configuration.prefixCommandsEnabled = true;

        CommandClient client = configuration.commandClient(properties, mock(ScheduledExecutorService.class), listener);

        assertEquals(",", client.getPrefix());
        assertTrue(client.getCommands().contains(whois));
        assertTrue(client.getCommands().contains(region));
        assertFalse(client.getCommands().contains(legacy));
        SlashCommand retiredSlashCommand = client.getSlashCommands().stream()
                .filter(command -> command.getName().equals("setrecruitrole")).findFirst().orElseThrow();
        assertTrue(retiredSlashCommand instanceof RetiredCommand);
        assertTrue(client.getCommands().contains(retiredSlashCommand));
        assertEquals(client.getSlashCommands(), client.getCommands());
        assertTrue(client.getSlashCommands().contains(whois));
        assertTrue(client.getSlashCommands().contains(region));
    }

    @Test
    void prefixCommandsAreOffByDefaultAndCanBeEnabledByTheEnvironmentVariable() {
        assertFalse(configurationFromEnvironment(Map.of()).prefixCommandsEnabled);
        assertTrue(configurationFromEnvironment(Map.of("DISCORD_PREFIX_COMMANDS_ENABLED", "true")).prefixCommandsEnabled);
        assertFalse(configurationFromEnvironment(Map.of("DISCORD_PREFIX_COMMANDS_ENABLED", "false")).prefixCommandsEnabled);
    }

    @Test
    void slashOnlyGatewayHasNoPrivilegedIntentsOrMessageListeners() {
        DiscordConfiguration configuration = new DiscordConfiguration();
        configuration.discordProperties = new DiscordProperties();
        CommandClient client = mock(CommandClient.class);
        FamilyScanListener scanner = mock(FamilyScanListener.class);

        DefaultShardManagerBuilder builder = configuration.shardManagerBuilder(client, scanner);

        assertTrue(intents(builder).isEmpty());
        assertSame(MemberCachePolicy.NONE, ReflectionTestUtils.getField(builder, "memberCachePolicy"));
        assertSame(ChunkingFilter.NONE, ReflectionTestUtils.getField(builder, "chunkingFilter"));
        assertEquals(List.of(client, scanner), ReflectionTestUtils.getField(builder, "listeners"));
    }

    @Test
    void prefixModeOnlyAddsMessageIntentsWithoutMemberCachingOrLegacyAutomation() {
        DiscordConfiguration configuration = new DiscordConfiguration();
        configuration.discordProperties = new DiscordProperties();
        configuration.prefixCommandsEnabled = true;
        CommandClient client = mock(CommandClient.class);
        FamilyScanListener scanner = mock(FamilyScanListener.class);

        DefaultShardManagerBuilder builder = configuration.shardManagerBuilder(client, scanner);

        assertEquals(Set.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES),
                intents(builder));
        assertSame(MemberCachePolicy.NONE, ReflectionTestUtils.getField(builder, "memberCachePolicy"));
        assertSame(ChunkingFilter.NONE, ReflectionTestUtils.getField(builder, "chunkingFilter"));
        List<?> listeners = (List<?>) ReflectionTestUtils.getField(builder, "listeners");
        assertEquals(List.of(client, scanner), listeners);
        assertFalse(listeners.stream().anyMatch(DiscordListener.class::isInstance));
    }

    @Test
    void globallyRegistersSlashCommandsOnStartupWithPrefixesDisabled() {
        assertGlobalStartupRegistration(false);
    }

    @Test
    void globallyRegistersSlashCommandsOnStartupWithPrefixesEnabled() {
        assertGlobalStartupRegistration(true);
    }

    private DiscordConfiguration configurationFromEnvironment(Map<String, Object> variables) {
        MockEnvironment environment = new MockEnvironment();
        environment.getPropertySources().addFirst(new SystemEnvironmentPropertySource(
                StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, variables));
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.setEnvironment(environment);
            context.registerBean(DiscordProperties.class);
            context.registerBean(RetiredCommand.class, () -> new RetiredCommand("test"));
            context.refresh();
            DiscordConfiguration configuration = new DiscordConfiguration();
            context.getAutowireCapableBeanFactory().autowireBean(configuration);
            return configuration;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void assertGlobalStartupRegistration(boolean prefixCommandsEnabled) {
        DiscordConfiguration configuration = new DiscordConfiguration();
        configuration.prefixCommandsEnabled = prefixCommandsEnabled;
        CommandListener listener = new CommandListener(mock(MetricsService.class));
        UserService users = mock(UserService.class);
        configuration.commandList = List.of(new WhoIsCommand(mock(FamilyService.class), users, listener),
                new SetDefaultRegionCommand(users, listener), new SetRecruitRoleCommand());
        DiscordProperties properties = new DiscordProperties();
        properties.setOwner("123456789012345678");
        CommandClient client = configuration.commandClient(properties, mock(ScheduledExecutorService.class), listener);
        JDA jda = mock(JDA.class);
        when(jda.getGuildCache()).thenReturn(mock(SnowflakeCacheView.class));
        SelfUser bot = mock(SelfUser.class);
        when(jda.getSelfUser()).thenReturn(bot);
        when(bot.isBot()).thenReturn(true);
        when(bot.getName()).thenReturn("BotDO");
        CommandListUpdateAction update = mock(CommandListUpdateAction.class);
        when(jda.updateCommands()).thenReturn(update);
        when(update.addCommands(anyCollection())).thenReturn(update);
        ReadyEvent ready = mock(ReadyEvent.class);
        when(ready.getJDA()).thenReturn(jda);

        ((EventListener) client).onEvent(ready);

        ArgumentCaptor<Collection<CommandData>> registered = ArgumentCaptor.forClass((Class) Collection.class);
        verify(jda).updateCommands();
        verify(jda, never()).getGuildById(anyString());
        verify(update).addCommands(registered.capture());
        verify(update).queue(any());
        assertEquals(Set.of("whois", "defaultregion", "setrecruitrole", "help"),
                registered.getValue().stream().map(CommandData::getName).collect(Collectors.toSet()));
    }

    private Set<GatewayIntent> intents(DefaultShardManagerBuilder builder) {
        return GatewayIntent.getIntents((Integer) ReflectionTestUtils.getField(builder, "intents"));
    }
}
