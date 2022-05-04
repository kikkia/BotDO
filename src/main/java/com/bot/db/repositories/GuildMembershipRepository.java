package com.bot.db.repositories;


import com.bot.db.entities.BDOGuildEntity;
import com.bot.db.entities.FamilyEntity;
import com.bot.db.entities.GuildMembershipEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildMembershipRepository extends CrudRepository<GuildMembershipEntity, Integer> {
    public boolean existsByFamilyAndGuildAndActive(FamilyEntity family, BDOGuildEntity guild, boolean active);
    public List<GuildMembershipEntity> getByGuildAndActive(BDOGuildEntity guild, boolean active);
}
