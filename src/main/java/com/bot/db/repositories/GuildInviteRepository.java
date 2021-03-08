package com.bot.db.repositories;

import com.bot.db.entities.GuildInviteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildInviteRepository extends CrudRepository<GuildInviteEntity, Integer> {
    public List<GuildInviteEntity> getAllByGuildId(String guildId);
    public void deleteByCode(String code);
}
