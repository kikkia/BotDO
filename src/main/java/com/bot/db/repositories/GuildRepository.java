package com.bot.db.repositories;

import com.bot.db.entities.GuildEntity;
import org.springframework.data.repository.CrudRepository;

public interface GuildRepository extends CrudRepository<GuildEntity, String> {
}
