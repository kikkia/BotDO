package com.bot.service;

import com.bot.db.entities.UserEntity;
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

    public UserEntity getById(String userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    public UserEntity setUserName(String id, String newName) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        UserEntity user = userOpt.get();
        user.setName(newName);
        return userRepository.save(user);
    }

    public UserEntity setFamilyName(String userId, String familyName) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        UserEntity user = userOpt.get();
        user.setFamilyName(familyName);
        return userRepository.save(user);
    }

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public UserEntity addUser(String id, String name) {
        var user = userRepository.save(new UserEntity(id, name));
        // Save default inventory
        if (!inventoryService.getInventoryExistsForUser(id)) {
            inventoryService.save(new ScrollInventory(user));
        }
        return user;
    }

    public List<UserEntity> getByIds(List<String> ids) {
        return userRepository.findAllById(ids);
    }
}
