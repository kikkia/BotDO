package com.bot.service;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.repositories.BdoGuildRepository;
import com.bot.models.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

@Transactional
@Service
public class BdoGuildService {
    @Autowired
    private BdoGuildRepository repository;

    public BDOGuildEntity createNewGuild(String name, Region region) {
        return repository.save(new BDOGuildEntity(0,
                name,
                region.getCode(),
                Timestamp.from(Instant.now()),
                Collections.emptyList(),
                null));
    }

    public Optional<BDOGuildEntity> getByNameAndRegion(String name, Region region) {
        return repository.findByNameAndRegion(name, region.getCode());
    }

    public Optional<BDOGuildEntity> getById(Integer id) {
        return repository.findById(id);
    }
}