package com.bot.service;

import com.bot.db.entities.ScrollGroup;
import com.bot.db.repositories.ScrollGroupRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScrollGroupService {
    @Autowired
    private ScrollGroupRepository scrollGroupRepository;

    public ScrollGroup save(ScrollGroup scrollGroup) {
        return scrollGroupRepository.save(scrollGroup);
    }

    public ScrollGroup create(Guild guild, String name) {
        var group = new ScrollGroup();
        group.setGuild(new com.bot.db.entities.GuildEntity(guild.getId(), guild.getName(),
                false, Collections.emptySet()));
        group.setName(name);
        group.setUsers(Collections.emptySet());
        return scrollGroupRepository.save(group);
    }

//    public ScrollGroup getForUserId(String userId) {
//        return scrollGroupRepository.findByUser_Id(userId);
//    }

    public Optional<ScrollGroup> getByGuildIdAndName(String guildId, String name) {
        return scrollGroupRepository.findByGuildIdAndName(guildId, name);
    }

    public List<ScrollGroup> getForGuildId(String guildId) {
        return scrollGroupRepository.findByGuildId(guildId);
    }

    public void removeById(int id) {
        scrollGroupRepository.deleteById(id);
    }
}
