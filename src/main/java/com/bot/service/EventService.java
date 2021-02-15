package com.bot.service;

import com.bot.db.entities.EventEntity;
import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.UserEntity;
import com.bot.db.mapper.EventRoleMapper;
import com.bot.db.repositories.EventRepository;
import com.bot.models.EventType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public List<EventEntity> getAllByGuildId(String guildId) {
        return eventRepository.findAllByGuildId(guildId);
    }

    public List<EventEntity> getAllUpcomingByGuildId(String guildId) {
        return eventRepository.findAllByGuildIdAndWithNextTimeAfter(guildId, Timestamp.from(Instant.now()));
    }

    public List<EventEntity> getAllPastByGuildId(String guildId) {
        return eventRepository.findAllByGuildIdAndWithNextTimeBefore(guildId, Timestamp.from(Instant.now()));
    }

    public EventEntity addNewCustomEvent(Guild guild, User author, String name, Instant time) {
        EventEntity eventEntity = new EventEntity(0,
                GuildEntity.Companion.partialFrom(guild),
                UserEntity.Companion.from(author),
                Timestamp.from(time),
                EventType.CUSTOM.getId(),
                name,
                Collections.emptyList());

        return eventRepository.save(eventEntity);
    }

    public EventEntity addRolesToEvent(EventEntity event, List<String> roleIds) {
        event.setRoles(roleIds.stream()
                .map(it -> EventRoleMapper.Companion.map(event, it))
                .collect(Collectors.toList()));
        return eventRepository.save(event);
    }
}
