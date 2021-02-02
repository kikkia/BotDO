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

    public User setUserName(String id, String newName) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOpt.get();
        user.setName(newName);
        return userRepository.save(user);
    }

    public User setFamilyName(String userId, String familyName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOpt.get();
        user.setFamilyName(familyName);
        return userRepository.save(user);
    }

    public User addUser(String id, String name) {
        var user = userRepository.save(new User(id, name));
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
