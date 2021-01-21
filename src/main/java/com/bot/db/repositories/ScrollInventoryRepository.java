package com.bot.db.repositories;

import com.bot.db.entities.ScrollInventory;
import org.springframework.data.repository.CrudRepository;

public interface ScrollInventoryRepository extends CrudRepository<ScrollInventory, Integer> {
    ScrollInventory findByUserId(String userId);
}
