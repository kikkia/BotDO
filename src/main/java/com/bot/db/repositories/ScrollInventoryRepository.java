package com.bot.db.repositories;

import com.bot.db.entities.ScrollInventoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScrollInventoryRepository extends CrudRepository<ScrollInventoryEntity, Integer> {
    ScrollInventoryEntity findByUserId(String userId);
    List<ScrollInventoryEntity> findByUserIds(List<String> userIds);
    boolean existsByUserId(String userId);
}
