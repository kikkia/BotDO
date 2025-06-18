package com.bot.service;

import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.UserEntity;
import com.bot.db.repositories.GuildRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;

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
        var members = new HashSet<UserEntity>();
        for (Member m : guild.getMembers()) {
            var existingUser = userService.getById(m.getUser().getId());
            if (existingUser == null) {
               members.add(userService.addUser(m.getUser().getId(),
                        m.getUser().getName(), m.getUser().getAvatarUrl()));
            } else {
                // Sync avatars
                existingUser.setAvatar(m.getUser().getAvatarUrl());
                members.add(userService.save(existingUser));
            }
        }

        GuildEntity guild1 = new GuildEntity(guild.getId(),
                guild.getName(),
                false,
                members);
        guild1.setAvatar(guild.getIconUrl());
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

    public GuildEntity setLogChannel(GuildEntity guildEntity, String channelId) {
        guildEntity.setLogChannel(channelId);
        return guildRepository.save(guildEntity);
    }

    public GuildEntity setRecruitMessage(GuildEntity guildEntity, String recruitMessage) {
        guildEntity.setRecruitMessage(recruitMessage);
        return guildRepository.save(guildEntity);
    }

    public GuildEntity setBdoGuild(GuildEntity guildEntity, BDOGuildEntity bdoGuildEntity) {
        guildEntity.setBdoGuild(bdoGuildEntity);
        return guildRepository.save(guildEntity);
    }

    public GuildEntity setArchiveChannel(GuildEntity guildEntity, String channelId) {
        guildEntity.setArchiveChannel(channelId);
        return guildRepository.save(guildEntity);
    }

    public GuildEntity setAvatar(GuildEntity guildEntity, String avatarUrl) {
        guildEntity.setAvatar(avatarUrl);
        return guildRepository.save(guildEntity);
    }
}
