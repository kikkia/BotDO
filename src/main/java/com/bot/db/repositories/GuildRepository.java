package com.bot.db.repositories;

import com.bot.db.entities.Guild;
import org.springframework.data.repository.CrudRepository;

public interface GuildRepository extends CrudRepository<Guild, String> {
}
