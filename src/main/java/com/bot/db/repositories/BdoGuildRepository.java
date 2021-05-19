package com.bot.db.repositories;

import com.bot.db.entities.BDOGuildEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BdoGuildRepository extends CrudRepository<BDOGuildEntity, Integer> {
    public Optional<BDOGuildEntity> findByNameAndRegion(String name, String region);
}