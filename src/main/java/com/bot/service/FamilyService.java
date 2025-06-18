package com.bot.service;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import com.bot.db.repositories.FamilyRepository;
import com.bot.db.repositories.GuildMembershipRepository;
import com.bot.models.Region;
import com.bot.utils.GuildScrapeUtils;
import com.bot.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@Slf4j
public class FamilyService {
    @Autowired
    private FamilyRepository repository;
    @Autowired
    private GuildMembershipRepository membershipRepository;
    @Autowired
    private BdoGuildService guildService;
    @Autowired
    private GuildScrapeUtils guildScrapeUtils;

    // Create a minimal family, no guild memberships or characters saved
    public FamilyEntity createMinimal(String familyName, String externalId, Region region) {
        return repository.save(new FamilyEntity(0,
                externalId,
                List.of(),
                familyName,
                region.getCode(),
                Timestamp.from(Instant.now()),
                false,
                0,
                false));
    }

    public Optional<FamilyEntity> getFamily(String familyName, Region region, boolean scrapeToSync) {
        var opt = repository.findByNameAndRegion(familyName, region.getCode());
        if (scrapeToSync && (opt.isEmpty() || opt.get().getLastUpdated().toInstant()
                .isBefore(Instant.now().minus(30, ChronoUnit.MINUTES)))) {
            try {
                // We dont have it, or is old entity, update the entity
                var syncedFromSite = syncSingleFromSite(familyName, region);
                // Fallback to db user in the case site is bugged
                if (syncedFromSite.isEmpty() && opt.isPresent()) {
                    return opt;
                }
                return syncSingleFromSite(familyName, region);
            } catch (Exception e) {
                log.warn("Hit error syncing from site, using db cached", e);
            }
        }
        return opt;
    }

    // For a given pageable return the page of family entities that are not private.
    // TODO: See if we can easily do a filter for "not in a guild" in the ORM layer
    public Page<FamilyEntity> getFamiliesNotPrivateAndPresentInRegion(String region, Pageable pageable) {
        return repository.findByPrivateAndMissingAndRegion(false, false, region, pageable);
    }

    public FamilyEntity save(FamilyEntity familyEntity) {
        return repository.save(familyEntity);
    }

    public FamilyEntity addToGuild(FamilyEntity familyEntity, BDOGuildEntity guildEntity) {
        // Check if we have an existing active
        if (membershipRepository.existsByFamilyAndGuildAndActive(familyEntity, guildEntity, true)) {
            return familyEntity; // They have active to this guild, dont add
        }

        var membershipToAdd = new GuildMembershipEntity(0,
                familyEntity,
                guildEntity,
                Timestamp.from(Instant.now()),
                true);
        // Un-active all new memberships
        for (GuildMembershipEntity membershipEntity : familyEntity.getMemberships()) {
            if (membershipEntity.getActive()) {
                if (Instant.now().isBefore(membershipEntity.getCreated().toInstant().plus(1, ChronoUnit.DAYS))) {
                    return familyEntity; // 24hr cooldown on guild join so do not add
                }
                membershipEntity.setActive(false);
                membershipRepository.save(membershipEntity);
            }
        }
        var newMems = new ArrayList<>(familyEntity.getMemberships());
        newMems.add(membershipToAdd);
        familyEntity.setMemberships(newMems);
        return save(familyEntity);
    }

    public FamilyEntity removeFromGuild(FamilyEntity familyEntity) {
        // Un-active all new memberships
        for (GuildMembershipEntity membershipEntity : familyEntity.getMemberships()) {
            if (membershipEntity.getActive()) {
                membershipEntity.setActive(false);
                membershipRepository.save(membershipEntity);
            }
        }
        var newMems = new ArrayList<>(familyEntity.getMemberships());
        familyEntity.setMemberships(newMems);
        return save(familyEntity);
    }

    public FamilyEntity updateExternalId(FamilyEntity entity, String newId) {
        entity.setExternalId(newId);
        return save(entity);
    }

    public Optional<FamilyEntity> syncSingleFromSite(String familyName, Region region) {
        var scrapedMemberOpt = guildScrapeUtils.getUserInfoForSearch(familyName, region);
        if (scrapedMemberOpt.isEmpty()) {
            return Optional.empty();
        }
        var scrapedMember = scrapedMemberOpt.get();

        // Check for an internally cached entity, if none we will make one
        var existing = getFamily(scrapedMember.getName(), region, false).orElseGet( () ->
                createMinimal(scrapedMember.getName(), scrapedMember.getId(), region)
        );

        var activeGuildName = existing.getMemberships().stream()
                .filter(GuildMembershipEntity::getActive)
                .map(GuildMembershipEntity::getGuild)
                .map(BDOGuildEntity::getName)
                .findFirst().orElse(null);
        if (!StringUtils.nullSafeEquals(scrapedMember.getGuild(), activeGuildName) && !scrapedMember.getPrivate()) {
            if (scrapedMember.getGuild() == null) {
                removeFromGuild(existing);
            } else {
                var guildOpt = guildService.getByNameAndRegion(scrapedMember.getGuild(), region);
                if (guildOpt.isEmpty()) {
                    log.warn("Could not find guild " + scrapedMember.getGuild() +
                            " when syncing individual family " + scrapedMember.getName());
                    // Create the guild to add them to
                    guildOpt = Optional.of(guildService.createNewGuild(scrapedMember.getGuild(), region));
                }
                existing = addToGuild(existing, guildOpt.get());
            }
        }
        existing.setPrivate(scrapedMember.getPrivate());
        existing.setLastUpdated(Timestamp.from(Instant.now()));
        return Optional.of(save(existing));
    }
}
