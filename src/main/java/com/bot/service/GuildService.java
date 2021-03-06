package com.bot.service;

import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.UserEntity;
import com.bot.db.mapper.UserMapper;
import com.bot.db.repositories.GuildRepository;
import net.dv8tion.jda.api.entities.Invite;
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

    public GuildEntity getById(String id) {
        Optional<GuildEntity> guild = guildRepository.findById(id);
        return guild.orElse(null);
    }

    public GuildEntity addUser(GuildEntity guild, UserEntity user) {
        guild.getUsers().add(user);
        return guildRepository.save(guild);
    }

    public GuildEntity removeUser(GuildEntity guild, UserEntity user) {
        guild.getUsers().remove(user);
        return guildRepository.save(guild);
    }

    public GuildEntity addFreshGuild(net.dv8tion.jda.api.entities.Guild guild) {
        for (Member m : guild.getMembers()) {
            if (userService.getById(m.getUser().getId()) == null) {
                userService.addUser(m.getUser().getId(),
                        m.getUser().getName());
            }
        }

        GuildEntity guild1 = new GuildEntity(guild.getId(),
                guild.getName(),
                false,
                guild.getMembers().stream()
                        .map(UserMapper.Companion::map)
                        .collect(Collectors.toSet()));
        GuildEntity internalGuild = guildRepository.save(guild1);

        for (TextChannel t : guild.getTextChannels()) {
            textChannelService.add(t, internalGuild);
        }

        return internalGuild;
    }

    public void rename(GuildEntity guild, String newName) {
        guild.setName(newName);
        guildRepository.save(guild);
    }

    public GuildEntity setSyncNames(GuildEntity guild, boolean enabled) {
        guild.setSyncNames(enabled);
        return guildRepository.save(guild);
    }

    public GuildEntity setRecruitRole(GuildEntity guildEntity, String roleId) {
        guildEntity.setRecruitRole(roleId);
        return guildRepository.save(guildEntity);
    }

    public GuildEntity setWelcomeChannel(GuildEntity guildEntity, String channelId) {
        guildEntity.setWelcomeChannel(channelId);
        return guildRepository.save(guildEntity);
    }

    public GuildEntity setEntryChannel(GuildEntity guildEntity, String channelId) {
        guildEntity.setEntryChannel(channelId);
        return guildRepository.save(guildEntity);
    }
}
