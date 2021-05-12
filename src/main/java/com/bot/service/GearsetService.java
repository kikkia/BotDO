package com.bot.service;

import com.bot.db.repositories.GearsetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@Slf4j
public class GearsetService {
    @Autowired
    private GearsetRepository repository;

    
}
