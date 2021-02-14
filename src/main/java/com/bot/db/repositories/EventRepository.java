package com.bot.db.repositories;

import com.bot.db.entities.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Integer> {
    List<Event> findByGuildId(String guildId);
}
