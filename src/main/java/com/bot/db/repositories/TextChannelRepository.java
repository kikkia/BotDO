package com.bot.db.repositories;

import com.bot.db.entities.TextChannel;
import org.springframework.data.repository.CrudRepository;

public interface TextChannelRepository extends CrudRepository<TextChannel, String> {
}
