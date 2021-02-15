package com.bot.db.repositories;

import com.bot.db.entities.EventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository extends CrudRepository<EventEntity, Integer> {
    List<EventEntity> findAllByGuildId(String guildId);
    // Get past events by guild id
    List<EventEntity> findAllByGuildIdAndWithNextTimeBefore(String guildId, @Param("nextTime") Timestamp time);
    // Get future events by guild id
    List<EventEntity> findAllByGuildIdAndWithNextTimeAfter(String guildId, @Param("nextTime") Timestamp time);
}