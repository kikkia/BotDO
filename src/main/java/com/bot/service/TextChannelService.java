package com.bot.service;

import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.TextChannel;
import com.bot.db.repositories.TextChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TextChannelService {
    @Autowired
    TextChannelRepository textChannelRepository;

    public TextChannel getById(String id) {
        Optional<TextChannel> channelOptional = textChannelRepository.findById(id);
        return channelOptional.orElse(null);
    }

    public TextChannel add(net.dv8tion.jda.api.entities.TextChannel channel,
                           GuildEntity guild) {
        TextChannel textChannel = new TextChannel(channel.getId(),
                guild,
                channel.getName(),
                false,
                false);
        return textChannelRepository.save(textChannel);
    }

    public TextChannel rename(TextChannel channel, String newName) {
        channel.setName(newName);
        return textChannelRepository.save(channel);
    }

    public void remove(TextChannel textChannel) {
        textChannelRepository.delete(textChannel);
    }

    public void removeById(String id) {
        textChannelRepository.deleteById(id);
    }
}
