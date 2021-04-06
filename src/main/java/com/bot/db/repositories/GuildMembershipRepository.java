package com.bot.db.repositories;


import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import org.springframework.data.repository.CrudRepository;

public interface GuildMembershipRepository extends CrudRepository<GuildMembershipEntity, Integer> {
    public boolean existsByFamilyAndGuildAndActive(FamilyEntity family, BDOGuildEntity guild, boolean active);
}
