package com.bot.configuration;

import com.bot.commands.RetiredCommand;
import com.bot.configuration.properties.DiscordProperties;
import com.bot.listeners.CommandListener;
import com.bot.listeners.FamilyScanListener;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.Compression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class DiscordConfiguration {

    @Autowired
    DiscordProperties discordProperties;

    @Autowired
    List<Command> commandList;

    @Value("${DISCORD_PREFIX_COMMANDS_ENABLED:false}")
    boolean prefixCommandsEnabled;

    @Bean
    public DefaultShardManagerBuilder shardManagerBuilder(CommandClient client,
                                                          FamilyScanListener listenerAdapter) {
        EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
        if (prefixCommandsEnabled) {
            intents = EnumSet.of(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.DIRECT_MESSAGES);
        }

        return DefaultShardManagerBuilder.createLight(discordProperties.getToken(), intents)
                .setShardsTotal(discordProperties.getShards())
                .setShards(discordProperties.getStartShard(), discordProperties.getEndShard())
                .setCompression(Compression.NONE)
                .addEventListeners(client, listenerAdapter)
                .setActivity(null)
                .setRequestTimeoutRetry(true)
                .setContextEnabled(false);
    }

    @Bean
    public ShardManager shardManager(DefaultShardManagerBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandClient commandClient(DiscordProperties properties,
                                       ScheduledExecutorService executorService,
                                       CommandListener commandListener) {
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setOwnerId(properties.getOwner());
        builder.useHelpBuilder(false);
        if (prefixCommandsEnabled) {
            builder.setPrefix(",");
            builder.setAlternativePrefix("@mention");
        }
        builder.setEmojis("\u2705", "\u2757", "\u274c");
        builder.setActivity(null);
        builder.setScheduleExecutor(executorService);
        builder.setListener(commandListener);
        // Automatic registration is global, covering every guild rather than one forced guild ID.
        builder.setManualUpsert(false);
        // Only lookup handlers execute; legacy commands supply names and aliases for retirement notices.
        for (Command command : commandList) {
            if (command instanceof SlashCommand) {
                registerCommand(builder, (SlashCommand) command);
            } else {
                registerCommand(builder, new RetiredCommand(command.getName()));
                for (String alias : command.getAliases()) {
                    registerCommand(builder, new RetiredCommand(alias));
                }
            }
        }
        registerCommand(builder, new RetiredCommand("help"));
        return builder.build();
    }

    private void registerCommand(CommandClientBuilder builder, SlashCommand command) {
        builder.addSlashCommand(command);
        if (prefixCommandsEnabled) {
            builder.addCommand(command);
        }
    }
}
