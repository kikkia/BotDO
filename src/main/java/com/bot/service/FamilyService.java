package com.bot.service;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import com.bot.db.repositories.FamilyRepository;
import com.bot.db.repositories.GuildMembershipRepository;
import com.bot.models.Region;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FamilyService {
    @Autowired
    private FamilyRepository repository;
    @Autowired
    private GuildMembershipRepository membershipRepository;

    // Create a minimal family, no guild memberships or characters saved
    public FamilyEntity createMinimal(String familyName, String externalId, Region region) {
        return repository.save(new FamilyEntity(0,
                externalId,
                List.of(),
                familyName,
                region.getCode(),
                Timestamp.from(Instant.now())));
    }

    public Optional<FamilyEntity> getFamily(String familyName, Region region) {
        return repository.findByNameAndRegion(familyName, region.getCode());
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

    public FamilyEntity updateExternalId(FamilyEntity entity, String newId) {
        entity.setExternalId(newId);
        return save(entity);
    }
}
