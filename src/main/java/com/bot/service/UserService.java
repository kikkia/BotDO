package com.bot.service;

import com.bot.db.entities.User;
import com.bot.db.repositories.UserRepository;
import com.bot.models.ScrollInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScrollInventoryService inventoryService;

    public User getById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    public User setFamilyName(String userId, String familyName) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user1 = user.get();
        user1.setFamilyName(familyName);
        return userRepository.save(user1);
    }

    public User addUser(String id, String name) {
        var user = userRepository.save(new User(id, name, name));
        // Save default inventory
        if (!inventoryService.getInventoryExistsForUser(id)) {
            inventoryService.save(new ScrollInventory(user));
        }
        return user;
    }

    public List<User> getByIds(List<String> ids) {
        return userRepository.findAllById(ids);
    }
}
