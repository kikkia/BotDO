package com.bot.service;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import com.bot.db.repositories.FamilyRepository;
import com.bot.db.repositories.GuildMembershipRepository;
import com.bot.models.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestFamilyService {
    private static final Region REGION = Region.NORTH_AMERICA;
    private final FamilyRepository repository = mock(FamilyRepository.class);
    private final GuildMembershipRepository memberships = mock(GuildMembershipRepository.class);
    private final FamilyService service = spy(new FamilyService());

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "membershipRepository", memberships);
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
    }

    @Test
    void returnsTheFirstSuccessfulRefreshWithoutFetchingAgain() {
        var cached = family();
        var refreshed = family();
        when(repository.findByNameAndRegion("TestFamily", "NA")).thenReturn(Optional.of(cached));
        doReturn(Optional.of(refreshed), Optional.empty()).when(service).syncSingleFromSite("TestFamily", REGION);

        assertSame(refreshed, service.getFamily("TestFamily", REGION, true).orElseThrow());

        verify(service, times(1)).syncSingleFromSite("TestFamily", REGION);
    }

    @Test
    void doesNotLoseANewFamilyByRepeatingTheSuccessfulLookup() {
        var refreshed = family();
        when(repository.findByNameAndRegion("TestFamily", "NA")).thenReturn(Optional.empty());
        doReturn(Optional.of(refreshed), Optional.empty()).when(service).syncSingleFromSite("TestFamily", REGION);

        assertSame(refreshed, service.getFamily("TestFamily", REGION, true).orElseThrow());

        verify(service, times(1)).syncSingleFromSite("TestFamily", REGION);
    }

    @Test
    void keepsTheCachedFamilyWhenTheSiteReturnsNoMatch() {
        var cached = family();
        when(repository.findByNameAndRegion("TestFamily", "NA")).thenReturn(Optional.of(cached));
        doReturn(Optional.empty()).when(service).syncSingleFromSite("TestFamily", REGION);

        assertSame(cached, service.getFamily("TestFamily", REGION, true).orElseThrow());

        verify(service, times(1)).syncSingleFromSite("TestFamily", REGION);
    }

    @Test
    void doesNotRepeatAnUncachedLookupWithNoMatch() {
        when(repository.findByNameAndRegion("TestFamily", "NA")).thenReturn(Optional.empty());
        doReturn(Optional.empty()).when(service).syncSingleFromSite("TestFamily", REGION);

        assertTrue(service.getFamily("TestFamily", REGION, true).isEmpty());

        verify(service, times(1)).syncSingleFromSite("TestFamily", REGION);
    }

    @Test
    void keepsTheCachedFamilyWhenRefreshingThrows() {
        var cached = family();
        when(repository.findByNameAndRegion("TestFamily", "NA")).thenReturn(Optional.of(cached));
        doThrow(new IllegalStateException("Site unavailable")).when(service).syncSingleFromSite("TestFamily", REGION);

        assertSame(cached, service.getFamily("TestFamily", REGION, true).orElseThrow());

        verify(service, times(1)).syncSingleFromSite("TestFamily", REGION);
    }

    @Test
    void doesNotRefreshAFreshCachedFamily() {
        var cached = family();
        cached.setLastUpdated(Timestamp.from(Instant.now()));
        when(repository.findByNameAndRegion("TestFamily", "NA")).thenReturn(Optional.of(cached));

        assertSame(cached, service.getFamily("TestFamily", REGION, true).orElseThrow());

        verify(service, never()).syncSingleFromSite(anyString(), any());
    }

    @Test
    void scopedRemovalOnlyDeactivatesMembershipsWithTheSameGuildId() {
        var family = family();
        var oldGuild = new BDOGuildEntity(1, "OldGuild", "NA", oldTimestamp());
        var newGuild = new BDOGuildEntity(2, "NewGuild", "NA", oldTimestamp());
        var oldMembership = new GuildMembershipEntity(1, family, oldGuild, oldTimestamp(), true);
        var newMembership = new GuildMembershipEntity(2, family, newGuild, oldTimestamp(), true);
        family.setMemberships(List.of(oldMembership, newMembership));
        var sameGuildId = new BDOGuildEntity(1, "OldGuild", "NA", oldTimestamp());

        service.removeFromGuild(family, sameGuildId);

        assertFalse(oldMembership.getActive());
        assertTrue(newMembership.getActive());
        verify(memberships).save(oldMembership);
        verify(memberships, never()).save(newMembership);
    }

    @Test
    void unscopedRemovalStillDeactivatesAllActiveMemberships() {
        var family = family();
        var guild = new BDOGuildEntity(1, "OldGuild", "NA", oldTimestamp());
        var membership = new GuildMembershipEntity(1, family, guild, oldTimestamp(), true);
        var otherGuild = new BDOGuildEntity(2, "OtherGuild", "NA", oldTimestamp());
        var otherMembership = new GuildMembershipEntity(2, family, otherGuild, oldTimestamp(), true);
        family.setMemberships(List.of(membership, otherMembership));

        service.removeFromGuild(family);

        assertFalse(membership.getActive());
        assertFalse(otherMembership.getActive());
        verify(memberships).save(membership);
        verify(memberships).save(otherMembership);
    }

    private FamilyEntity family() {
        return new FamilyEntity(1, "family-id", List.of(), "TestFamily", "NA", oldTimestamp(), false, 0, false);
    }

    private Timestamp oldTimestamp() {
        return Timestamp.from(Instant.now().minusSeconds(172800));
    }
}
