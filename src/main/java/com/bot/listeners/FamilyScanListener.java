package com.bot.listeners;

import com.bot.configuration.properties.DiscordProperties;
import com.bot.models.Region;
import com.bot.service.BdoGuildService;
import com.bot.service.FamilyService;
import com.bot.service.MetricsService;
import com.bot.tasks.ScanGuildsTask;
import com.bot.utils.GuildScrapeUtils;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class FamilyScanListener extends ListenerAdapter {

    private final DiscordProperties discordProperties;
    private final ScheduledExecutorService executorService;
    private final FamilyService familyService;
    private final BdoGuildService bdoGuildService;
    private final MetricsService metricsService;
    private final GuildScrapeUtils guildScrapeUtils;
    private final AtomicBoolean scansScheduled = new AtomicBoolean();

    public FamilyScanListener(DiscordProperties discordProperties,
                              ScheduledExecutorService executorService,
                              FamilyService familyService,
                              BdoGuildService bdoGuildService,
                              MetricsService metricsService,
                              GuildScrapeUtils guildScrapeUtils) {
        this.discordProperties = discordProperties;
        this.executorService = executorService;
        this.familyService = familyService;
        this.bdoGuildService = bdoGuildService;
        this.metricsService = metricsService;
        this.guildScrapeUtils = guildScrapeUtils;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!discordProperties.getScanFamilies() || !scansScheduled.compareAndSet(false, true)) {
            return;
        }

        // Ready fires for each shard; index BDO history only once for the whole bot.
        executorService.scheduleAtFixedRate(
                new ScanGuildsTask(familyService, bdoGuildService, metricsService, guildScrapeUtils, Region.NORTH_AMERICA),
                0, 24, TimeUnit.HOURS);
        executorService.scheduleAtFixedRate(
                new ScanGuildsTask(familyService, bdoGuildService, metricsService, guildScrapeUtils, Region.EUROPE),
                12, 24, TimeUnit.HOURS);
    }
}
