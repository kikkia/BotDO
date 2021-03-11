package com.bot.service;

import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.RecruitmentPostEntity;
import com.bot.db.entities.UserEntity;
import com.bot.db.repositories.RecruitmentPostRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class RecruitmentPostService {

    @Autowired
    private RecruitmentPostRepository recruitmentPostRepository;

    public RecruitmentPostEntity add(User author, Guild guild, String channel) {
        return recruitmentPostRepository.save(new RecruitmentPostEntity(0,
                UserEntity.Companion.from(author),
                GuildEntity.Companion.partialFrom(guild),
                channel,
                Timestamp.from(Instant.now())));
    }
}
