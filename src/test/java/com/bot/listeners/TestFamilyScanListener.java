package com.bot.listeners;

import com.bot.configuration.properties.BdoSiteProperties;
import com.bot.configuration.properties.DiscordProperties;
import com.bot.service.BdoGuildService;
import com.bot.service.FamilyService;
import com.bot.service.MetricsService;
import com.bot.tasks.ScanGuildsTask;
import com.bot.utils.GuildScrapeUtils;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestFamilyScanListener {

    private final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    private final DiscordProperties properties = new DiscordProperties();
    private final FamilyScanListener listener = new FamilyScanListener(properties, executor,
            mock(FamilyService.class), mock(BdoGuildService.class), mock(MetricsService.class),
            new GuildScrapeUtils(new BdoSiteProperties()));

    @Test
    void schedulesNaAndEuIndexingOnlyOnceAcrossShards() {
        listener.onReady(mock(ReadyEvent.class));
        listener.onReady(mock(ReadyEvent.class));

        verify(executor).scheduleAtFixedRate(any(ScanGuildsTask.class), eq(0L), eq(24L), eq(TimeUnit.HOURS));
        verify(executor).scheduleAtFixedRate(any(ScanGuildsTask.class), eq(12L), eq(24L), eq(TimeUnit.HOURS));
        verifyNoMoreInteractions(executor);
    }

    @Test
    void honorsTheExistingScanFamiliesSetting() {
        properties.setScanFamilies(false);

        listener.onReady(mock(ReadyEvent.class));

        verifyNoInteractions(executor);
    }

    @Test
    void legacyDiscordAutomationIsNotASpringComponent() {
        assertFalse(DiscordListener.class.isAnnotationPresent(Component.class));
    }
}
