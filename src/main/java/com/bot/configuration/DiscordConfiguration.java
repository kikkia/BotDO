package com.bot.configuration;

import com.bot.listeners.CommandListener;
import com.bot.listeners.DiscordListener;
import com.bot.configuration.properties.DiscordProperties;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES;

@Configuration
public class DiscordConfiguration {

    @Autowired
    DiscordProperties discordProperties;

    @Autowired
    List<Command> commandList;

    @Bean
    public ShardManager shardManager(EventWaiter eventWaiter,
                                     CommandClient client,
                                     DiscordListener listenerAdapter) throws LoginException {
        return DefaultShardManagerBuilder
                .createDefault(
                        discordProperties.getToken(),
                        GUILD_MEMBERS,
                        GUILD_MESSAGES,
                        GUILD_EMOJIS_AND_STICKERS,
                        GUILD_INVITES,
                        GUILD_MESSAGE_REACTIONS,
                        GUILD_VOICE_STATES,
                        DIRECT_MESSAGES,
                        DIRECT_MESSAGE_REACTIONS
                )
                .setShardsTotal(discordProperties.getShards())
                .setChunkingFilter(ChunkingFilter.ALL)
                .setShards(discordProperties.getStartShard(), discordProperties.getEndShard())
                .setCompression(Compression.NONE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(client, eventWaiter, listenerAdapter)
                .setActivity(null)
                .setRequestTimeoutRetry(true)
                .setContextEnabled(false)
                .build();
    }

    @Bean
    public CommandClient commandClient(DiscordProperties properties,
                                       ScheduledExecutorService executorService,
                                       CommandListener commandListener) {
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(",");
        builder.setAlternativePrefix("@mention");
        builder.setOwnerId(properties.getOwner());
        builder.useHelpBuilder(true);
        builder.setEmojis("\u2705", "\u2757", "\u274c");
        builder.setActivity(null);
        builder.setScheduleExecutor(executorService);
        builder.setListener(commandListener);
        builder.addCommands(commandList.toArray(new Command[commandList.size()]));
        return builder.build();
    }
}
