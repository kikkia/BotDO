package com.bot.db.repositories;

import com.bot.db.entities.FamilyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FamilyRepository extends CrudRepository<FamilyEntity, Integer> {
    public Optional<FamilyEntity> findByNameAndRegion(String name, String region);
}
