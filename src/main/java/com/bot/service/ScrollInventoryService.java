package com.bot.service;

import com.bot.db.mapper.ScrollInventoryMapper;
import com.bot.db.repositories.ScrollInventoryRepository;
import com.bot.models.ScrollInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScrollInventoryService {

    @Autowired
    private ScrollInventoryRepository inventoryRepository;

    public ScrollInventory getByUser(String userId) {
        return ScrollInventoryMapper.Companion.map(inventoryRepository.findByUserId(userId));
    }

    public ScrollInventory save(ScrollInventory inventory) {
        var inventoryEntity = inventoryRepository.save(
                ScrollInventoryMapper.Companion.map(inventory)
        );
        return ScrollInventoryMapper.Companion.map(inventoryEntity);
    }

    public List<ScrollInventory> getByUserIds(List<String> userIds) {
        return inventoryRepository.findByUserIds(userIds).stream()
                .map(ScrollInventoryMapper.Companion::map)
                .collect(Collectors.toList());
    }

    public boolean getInventoryExistsForUser(String userId) {
        return inventoryRepository.existsByUserId(userId);
    }
}
