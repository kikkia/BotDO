package com.bot.db.repositories;

import com.bot.db.entities.TextChannelEntity;
import org.springframework.data.repository.CrudRepository;

public interface TextChannelRepository extends CrudRepository<TextChannelEntity, String> {
}
