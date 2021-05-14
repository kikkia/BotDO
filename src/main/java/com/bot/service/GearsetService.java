package com.bot.service;

import com.bot.db.entities.GearsetEntity;
import com.bot.db.repositories.GearsetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
@Slf4j
public class GearsetService {
    @Autowired
    private GearsetRepository repository;

    public Optional<GearsetEntity> getById(Integer id) {
        return repository.findById(id);
    }

    public GearsetEntity save(GearsetEntity entity) {
        return repository.save(entity);
    }
}
