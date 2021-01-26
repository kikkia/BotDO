package com.bot.db.repositories;

import com.bot.db.entities.ScrollHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScrollHistoryRepository extends CrudRepository<ScrollHistoryEntity, Integer> {
    List<ScrollHistoryEntity> findAllByUserId(String userId);
}
