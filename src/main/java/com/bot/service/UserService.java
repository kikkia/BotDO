package com.bot.service;

import com.bot.db.entities.User;
import com.bot.db.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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
        User user = new User(id, name, name);
        return userRepository.save(user);
    }
}
