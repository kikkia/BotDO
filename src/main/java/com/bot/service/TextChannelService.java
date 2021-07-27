package com.bot.service;

import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.TextChannelEntity;
import com.bot.db.repositories.TextChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TextChannelService {
    @Autowired
    TextChannelRepository textChannelRepository;

    public TextChannelEntity getById(String id) {
        Optional<TextChannelEntity> channelOptional = textChannelRepository.findById(id);
        return channelOptional.orElse(null);
    }

    public TextChannelEntity add(net.dv8tion.jda.api.entities.TextChannel channel,
                                 GuildEntity guild) {
        TextChannelEntity textChannelEntity = new TextChannelEntity(channel.getId(),
                guild.getId(),
                channel.getName(),
                false,
                false);
        return textChannelRepository.save(textChannelEntity);
    }

    public TextChannelEntity rename(TextChannelEntity channel, String newName) {
        channel.setName(newName);
        return textChannelRepository.save(channel);
    }

    public void remove(TextChannelEntity textChannelEntity) {
        textChannelRepository.delete(textChannelEntity);
    }

    public void removeById(String id) {
        textChannelRepository.deleteById(id);
    }
}
