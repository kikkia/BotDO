package com.bot.service;

import com.bot.db.entities.FamilyEntity;
import com.bot.db.repositories.FamilyRepository;
import com.bot.models.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

@Transactional
@Service
public class FamilyService {
    @Autowired
    private FamilyRepository repository;

    // Create a minimal family, no guild memberships or characters saved
    public FamilyEntity createMinimal(String familyName, String externalId, Region region) {
        return repository.save(new FamilyEntity(0,
                externalId,
                Collections.emptyList(),
                familyName,
                region.getCode(),
                Collections.emptyList()));
    }

    public Optional<FamilyEntity> getFamily(String familyName, Region region) {
        return repository.findByNameAndRegion(familyName, region.getCode());
    }
}
