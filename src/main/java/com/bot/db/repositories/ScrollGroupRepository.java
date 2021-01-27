package com.bot.db.repositories;

import com.bot.db.entities.ScrollGroup;
import com.bot.db.entities.User;
import com.bot.models.Scroll;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ScrollGroupRepository extends CrudRepository<ScrollGroup, Integer> {
    // ScrollGroup findByUser_Id(String userId);
    List<ScrollGroup> findByGuildId(String guildId);
    Optional<ScrollGroup> findByGuildIdAndName(String guildId, String name);
}
