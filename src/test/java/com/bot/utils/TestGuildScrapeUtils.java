package com.bot.utils;

import com.bot.configuration.properties.BdoSiteProperties;
import com.bot.models.Region;
import com.bot.service.MetricsService;
import com.sun.net.httpserver.HttpServer;
import com.timgroup.statsd.StatsDClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestGuildScrapeUtils {
    private final StatsDClient stats = mock(StatsDClient.class);
    private HttpServer server;
    private GuildScrapeUtils scraper;
    private volatile int status = 200;
    private volatile boolean disconnect;
    private volatile String responseBody = "<a href=\"?guildName=TestGuild&region=NA\">TestGuild</a>" +
            "<a href=\"/Adventure/Profile?profileTarget=family-id\">TestFamily</a>";

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            if (disconnect) {
                exchange.close();
                return;
            }
            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        var properties = new BdoSiteProperties();
        properties.setBdoSiteBaseUrl(List.of("http://127.0.0.1:" + server.getAddress().getPort()));
        scraper = new GuildScrapeUtils(properties, new MetricsService(stats));
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    static Stream<Arguments> httpResults() {
        return Stream.of("guild_search", "guild_profile", "family_search")
                .flatMap(endpoint -> Stream.of(200, 403, 429, 500).map(status -> Arguments.of(endpoint, status)));
    }

    @ParameterizedTest
    @MethodSource("httpResults")
    void recordsEachHttpResultBeforeStatusValidation(String endpoint, int responseStatus) {
        status = responseStatus;

        if (status == 200) {
            assertDoesNotThrow(() -> scrape(endpoint));
        } else {
            assertThrows(IOException.class, () -> scrape(endpoint));
        }

        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:" + endpoint, "status_code:" + status);
        verifyNoMoreInteractions(stats);
    }

    @Test
    void recordsFailuresWithoutAnHttpResponseOnceAndStillThrows() {
        disconnect = true;

        assertThrows(IOException.class, () -> scraper.getUserInfoForSearch("TestFamily", Region.NORTH_AMERICA));

        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:family_search", "status_code:none");
        verifyNoMoreInteractions(stats);
    }

    @Test
    void recordsTheCurrentlyUnusedCharacterProfileEndpointToo() throws ReflectiveOperationException {
        status = 403;
        var method = GuildScrapeUtils.class.getDeclaredMethod("getCharactersForFamily", String.class, Region.class);
        method.setAccessible(true);

        var exception = assertThrows(InvocationTargetException.class,
                () -> method.invoke(scraper, "family-id", Region.NORTH_AMERICA));

        assertTrue(exception.getCause() instanceof IOException);
        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:family_profile", "status_code:403");
        verifyNoMoreInteractions(stats);
    }

    @Test
    void keepsTheHttpStatusWhenParsingTheResponseFails() throws ReflectiveOperationException {
        responseBody = "<div class=\"character_txt\">Missing character fields</div>";
        var method = GuildScrapeUtils.class.getDeclaredMethod("getCharactersForFamily", String.class, Region.class);
        method.setAccessible(true);

        var exception = assertThrows(InvocationTargetException.class,
                () -> method.invoke(scraper, "family-id", Region.NORTH_AMERICA));

        assertTrue(exception.getCause() instanceof NullPointerException);
        verify(stats).incrementCounter("bdo.scrape.request", "endpoint:family_profile", "status_code:200");
        verifyNoMoreInteractions(stats);
    }

    private void scrape(String endpoint) {
        switch (endpoint) {
            case "guild_search" -> scraper.getGuildNamesOnPage(1, "TestGuild", Region.NORTH_AMERICA);
            case "guild_profile" -> scraper.getGuildFamilies("TestGuild", Region.NORTH_AMERICA);
            case "family_search" -> scraper.getUserInfoForSearch("TestFamily", Region.NORTH_AMERICA);
            default -> throw new IllegalArgumentException("Unknown endpoint " + endpoint);
        }
    }
}
