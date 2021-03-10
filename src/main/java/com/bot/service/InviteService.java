package com.bot.service;

import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.GuildInviteEntity;
import com.bot.db.entities.InviteRoleEntity;
import com.bot.db.repositories.GuildInviteRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InviteService {

    @Autowired
    private GuildInviteRepository guildInviteRepository;

    public void save(GuildInviteEntity inviteEntity) {
        guildInviteRepository.save(inviteEntity);
    }

    public void addExisting(Invite invite) {
        var guildInvite = new GuildInviteEntity(0, invite.getCode(),
                GuildEntity.Companion.partialFrom(
                    Objects.requireNonNull(invite.getJDA().getGuildById(invite.getGuild().getId()))),
                invite.getUses(),
                invite.getMaxUses(),
                Collections.emptyList(),
                invite.getInviter().getId(),
                Timestamp.from(invite.getTimeCreated().toInstant()));
        guildInviteRepository.save(guildInvite);
    }

    public GuildInviteEntity add(Guild guild, Invite invite, List<String> roleIds, String author) {
        return add(guild, invite, roleIds, null, null, author);
    }

    public GuildInviteEntity add(Guild guild, Invite invite, List<String> roleIds, String welcomeMessage, String guildName, String author) {
        var inviteEntity = new GuildInviteEntity(0,
                invite.getCode(),
                GuildEntity.Companion.partialFrom(guild),
                invite.getUses(),
                invite.getMaxUses(),
                new ArrayList<>(),
                author,
                Timestamp.from(invite.getTimeCreated().toInstant()));
        inviteEntity.setGuildPrefix(guildName);
        inviteEntity.setWelcomeMessage(welcomeMessage);
        final var guildInvite = guildInviteRepository.save(inviteEntity);
        guildInvite.setRoles(roleIds.stream().map(
                it -> new InviteRoleEntity(0, it, guildInvite))
                .collect(Collectors.toList()));
        return guildInviteRepository.save(guildInvite);
    }

    public Map<String, GuildInviteEntity> getMapForGuildId(String guildId) {
        var all = guildInviteRepository.getAllByGuildId(guildId);
        return all.stream().collect(Collectors.toMap(GuildInviteEntity::getCode, Function.identity()));
    }

    public void removeByCode(String code) {
        guildInviteRepository.deleteByCode(code);
    }

    public void remove(GuildInviteEntity guildInviteEntity) {
        guildInviteRepository.delete(guildInviteEntity);
    }
}
