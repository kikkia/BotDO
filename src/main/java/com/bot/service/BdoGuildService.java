package com.bot.service;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.repositories.BdoGuildRepository;
import com.bot.db.repositories.GuildMembershipRepository;
import com.bot.models.Region;
import com.bot.models.WarDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class BdoGuildService {
    @Autowired
    private BdoGuildRepository repository;
    @Autowired
    private GuildMembershipRepository membershipRepository;

    public BDOGuildEntity createNewGuild(String name, Region region) {
        return repository.save(new BDOGuildEntity(0,
                name,
                region.getCode(),
                Timestamp.from(Instant.now())));
    }

    public Optional<BDOGuildEntity> getByNameAndRegion(String name, Region region) {
        return repository.findByNameAndRegion(name, region.getCode());
    }

    public Optional<BDOGuildEntity> getById(Integer id) {
        return repository.findById(id);
    }

    public Page<BDOGuildEntity> getAllByRegion(Region region, int limit, int offset) {
        return repository.findAllByRegion(region.getCode(), PageRequest.of((offset / limit), limit));
    }

    public List<String> getAllFamilyNamesInGuild(BDOGuildEntity guildEntity) {
        return membershipRepository.getByGuildAndActive(guildEntity, true).stream().map(it -> it.getFamily().getName()).collect(Collectors.toList());
    }

    public BDOGuildEntity setWarDays(BDOGuildEntity bdoGuildEntity, List<WarDay> days) {
        bdoGuildEntity.setWarDays(days.stream().mapToInt(WarDay::getId).sum());
        return save(bdoGuildEntity);
    }

    public BDOGuildEntity setScan(BDOGuildEntity guildEntity) {
        guildEntity.setLast_scan(Timestamp.from(Instant.now()));
        return save(guildEntity);
    }

    public BDOGuildEntity save(BDOGuildEntity entity) {
        return repository.save(entity);
    }
}
