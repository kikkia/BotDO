package com.bot.db.repositories;

import com.bot.db.entities.BDOGuildEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface BdoGuildRepository extends PagingAndSortingRepository<BDOGuildEntity, Integer> {
    public Optional<BDOGuildEntity> findByNameAndRegion(String name, String region);
    public Page<BDOGuildEntity> findAllByRegion(String region, Pageable pageable);
}