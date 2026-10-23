package com.bot.tasks;

import com.bot.configuration.properties.BdoSiteProperties;
import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import com.bot.db.repositories.FamilyRepository;
import com.bot.db.repositories.GuildMembershipRepository;
import com.bot.models.Region;
import com.bot.service.BdoGuildService;
import com.bot.service.FamilyService;
import com.bot.service.MetricsService;
import com.bot.utils.GuildScrapeUtils;
import com.sun.net.httpserver.HttpServer;
import com.timgroup.statsd.StatsDClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestScanGuildsTask {
    private static final Region REGION = Region.NORTH_AMERICA;
    private final BdoGuildService guildService = mock(BdoGuildService.class);
    private final FamilyRepository repository = mock(FamilyRepository.class);
    private final GuildMembershipRepository memberships = mock(GuildMembershipRepository.class);
    private final StatsDClient stats = mock(StatsDClient.class);
    private final FamilyService familyService = new FamilyService();
    private final Map<String, FamilyEntity> families = new HashMap<>();
    private final List<BDOGuildEntity> guilds = new ArrayList<>();
    private final BDOGuildEntity guild = guild(1);
    private final AtomicInteger rosterRequests = new AtomicInteger();
    private final AtomicInteger profileRequests = new AtomicInteger();
    private HttpServer server;
    private ScanGuildsTask task;
    private volatile int rosterStatus = 200;
    private volatile int profileStatus = 200;
    private volatile String rosterBody = "";
    private volatile String profileBody = "";

    @BeforeEach
    void setUp() throws IOException {
        guilds.add(guild);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            boolean roster = exchange.getRequestURI().getPath().contains("GuildProfile");
            (roster ? rosterRequests : profileRequests).incrementAndGet();
            byte[] body = (roster ? rosterBody : profileBody).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(roster ? rosterStatus : profileStatus, body.length == 0 ? -1 : body.length);
            try (var output = exchange.getResponseBody()) {
                if (body.length > 0) {
                    output.write(body);
                }
            }
        });
        server.start();
        var properties = new BdoSiteProperties();
        properties.setBdoSiteBaseUrl(List.of("http://127.0.0.1:" + server.getAddress().getPort()));
        var metrics = new MetricsService(stats);
        var scraper = new GuildScrapeUtils(properties, metrics);
        ReflectionTestUtils.setField(familyService, "repository", repository);
        ReflectionTestUtils.setField(familyService, "membershipRepository", memberships);
        ReflectionTestUtils.setField(familyService, "guildService", guildService);
        ReflectionTestUtils.setField(familyService, "guildScrapeUtils", scraper);
        task = new ScanGuildsTask(familyService, guildService, metrics, scraper, REGION);

        when(guildService.getAllByRegion(eq(REGION), any(Pageable.class))).thenAnswer(call -> {
            Pageable page = call.getArgument(1);
            int start = Math.min((int) page.getOffset(), guilds.size());
            int end = Math.min(start + page.getPageSize(), guilds.size());
            return new PageImpl<>(new ArrayList<>(guilds.subList(start, end)), page, guilds.size());
        });
        when(guildService.getAllFamilyNamesInGuild(any())).thenAnswer(call -> {
            BDOGuildEntity scannedGuild = call.getArgument(0);
            return new ArrayList<>(families.values().stream()
                    .filter(family -> family.getMemberships().stream().anyMatch(membership ->
                            membership.getActive() && membership.getGuild().getId() == scannedGuild.getId()))
                    .map(FamilyEntity::getName).toList());
        });
        when(guildService.setScan(any())).thenAnswer(call -> {
            BDOGuildEntity scannedGuild = call.getArgument(0);
            scannedGuild.setLast_scan(Timestamp.from(Instant.now()));
            return scannedGuild;
        });
        when(repository.findByNameAndRegion(anyString(), eq("NA")))
                .thenAnswer(call -> Optional.ofNullable(families.get(call.getArgument(0))));
        when(repository.save(any())).thenAnswer(call -> {
            FamilyEntity family = call.getArgument(0);
            families.put(family.getName(), family);
            return family;
        });
        when(memberships.save(any())).thenAnswer(call -> call.getArgument(0));
        when(memberships.existsByFamilyAndGuildAndActive(any(), any(), eq(true))).thenAnswer(call -> {
            FamilyEntity family = call.getArgument(0);
            BDOGuildEntity targetGuild = call.getArgument(1);
            return family.getMemberships().stream().anyMatch(membership ->
                    membership.getActive() && membership.getGuild().getId() == targetGuild.getId());
        });
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 101})
    void scansEveryDatabasePageAndHandlesQuickOrEmptyRuns(int guildCount) {
        guilds.clear();
        for (int id = 1; id <= guildCount; id++) {
            guilds.add(guild(id));
        }

        assertDoesNotThrow(task::run);

        assertEquals(guildCount, rosterRequests.get());
        verify(guildService, times(guildCount)).setScan(any());
        verify(stats, times(guildCount)).incrementCounter("bdo.guild.update");
        verify(stats, times(guildCount)).incrementCounter(
                "bdo.scrape.request", "endpoint:guild_profile", "status_code:200");
        verify(guildService, times(guildCount > 100 ? 2 : 1)).getAllByRegion(eq(REGION), any());
    }

    @Test
    void aForbiddenRosterDoesNotAdvanceLastScanAndCanBeRetriedImmediately() {
        var lastScan = guild.getLast_scan();
        rosterStatus = 403;

        assertDoesNotThrow(task::run);

        assertEquals(lastScan, guild.getLast_scan());
        verify(guildService, never()).setScan(any());
        verify(guildService, never()).getAllFamilyNamesInGuild(any());
        verify(stats, never()).incrementCounter("bdo.guild.update");
        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:guild_profile", "status_code:403");

        rosterStatus = 200;
        task.run();

        assertEquals(2, rosterRequests.get());
        assertTrue(guild.getLast_scan().after(lastScan));
        verify(guildService).setScan(guild);
        verify(stats).incrementCounter("bdo.guild.update");
    }

    @Test
    void recentlyScannedGuildsDoNotMakeHttpRequests() {
        guild.setLast_scan(Timestamp.from(Instant.now()));

        assertDoesNotThrow(task::run);

        assertEquals(0, rosterRequests.get());
        verify(guildService, never()).setScan(any());
        verifyNoInteractions(stats);
    }

    @Test
    void failedDepartureReconciliationDoesNotMarkTheGuildComplete() {
        member("Leaver");
        var lastScan = guild.getLast_scan();
        when(repository.findByNameAndRegion("Leaver", "NA")).thenThrow(new IllegalStateException("Database unavailable"));

        assertDoesNotThrow(task::run);

        assertEquals(lastScan, guild.getLast_scan());
        verify(guildService, never()).setScan(any());
        verify(stats, never()).incrementCounter("bdo.guild.update");
    }

    @Test
    void departureRefreshPreservesTheNewGuildAndOnlyFetchesTheProfileOnce() {
        member("Remaining");
        var leaver = member("Leaver");
        var newGuild = guild(2);
        rosterBody = profileLink("Remaining");
        profileBody = "<a href=\"?guildName=Guild2&region=NA\">Guild2</a>" + profileLink("Leaver");
        when(guildService.getByNameAndRegion("Guild2", REGION)).thenReturn(Optional.of(newGuild));

        task.run();

        assertEquals(1, profileRequests.get());
        assertTrue(leaver.getMemberships().stream().anyMatch(membership ->
                membership.getGuild().getId() == newGuild.getId() && membership.getActive()));
        assertTrue(leaver.getMemberships().stream().noneMatch(membership ->
                membership.getGuild().getId() == guild.getId() && membership.getActive()));
        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:family_search", "status_code:200");
        verify(guildService).setScan(guild);
    }

    @Test
    void forbiddenProfileIsCountedDespiteCacheFallbackAndCannotClearAnotherGuild() {
        var leaver = member("Leaver");
        var newGuild = guild(2);
        leaver.getMemberships().add(new GuildMembershipEntity(2, leaver, newGuild, oldTimestamp(), true));
        profileStatus = 403;

        task.run();

        assertEquals(1, profileRequests.get());
        assertFalse(leaver.getMemberships().get(0).getActive());
        assertTrue(leaver.getMemberships().get(1).getActive());
        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:family_search", "status_code:403");
        verify(stats, never()).incrementCounter("bdo.scrape.request", "endpoint:family_search", "status_code:none");
    }

    private FamilyEntity member(String name) {
        var family = new FamilyEntity(families.size() + 1, name + "-id", new ArrayList<>(),
                name, "NA", oldTimestamp(), false, 0, false);
        family.getMemberships().add(new GuildMembershipEntity(families.size() + 1, family, guild, oldTimestamp(), true));
        families.put(name, family);
        return family;
    }

    private BDOGuildEntity guild(int id) {
        return new BDOGuildEntity(id, "Guild" + id, "NA", oldTimestamp());
    }

    private Timestamp oldTimestamp() {
        return Timestamp.from(Instant.now().minusSeconds(172800));
    }

    private String profileLink(String name) {
        return "<a href=\"/Adventure/Profile?profileTarget=" + name + "-id\">" + name + "</a>";
    }
}
