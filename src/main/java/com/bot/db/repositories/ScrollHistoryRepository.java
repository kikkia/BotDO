package com.bot.db.repositories;

import com.bot.db.entities.ScrollHistoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface ScrollHistoryRepository extends CrudRepository<ScrollHistoryEntity, Integer> {
    ScrollHistoryEntity findByUserId(String userId);
}
