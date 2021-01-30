package com.bot.service;

import com.bot.db.entities.Guild;
import com.bot.db.entities.User;
import com.bot.db.mapper.UserMapper;
import com.bot.db.repositories.GuildRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class GuildService {

    @Autowired
    GuildRepository guildRepository;

    @Autowired
    UserService userService;

    @Autowired
    TextChannelService textChannelService;

    public Guild getById(String id) {
        Optional<Guild> guild = guildRepository.findById(id);
        return guild.orElse(null);
    }

    public Guild addUser(Guild guild, User user) {
        guild.getUsers().add(user);
        return guildRepository.save(guild);
    }

    public Guild removeUser(Guild guild, User user) {
        guild.getUsers().remove(user);
        return guildRepository.save(guild);
    }

    public Guild addFreshGuild(net.dv8tion.jda.api.entities.Guild guild) {
        for (Member m : guild.getMembers()) {
            if (userService.getById(m.getUser().getId()) == null) {
                userService.addUser(m.getUser().getId(),
                        m.getUser().getName());
            }
        }

        Guild guild1 = new Guild(guild.getId(),
                guild.getName(),
                false,
                guild.getMembers().stream()
                        .map(UserMapper.Companion::map)
                        .collect(Collectors.toSet()));
        Guild internalGuild = guildRepository.save(guild1);

        for (TextChannel t : guild.getTextChannels()) {
            textChannelService.add(t, internalGuild);
        }

        return internalGuild;
    }

    public void rename(Guild guild, String newName) {
        guild.setName(newName);
        guildRepository.save(guild);
    }

    public Guild setSyncNames(Guild guild, boolean enabled) {
        guild.setSyncNames(enabled);
        return guildRepository.save(guild);
    }
}
