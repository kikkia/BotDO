package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        val id: String,
        val name: String,
        val familyName: String,
        @ManyToOne
        @JoinTable(name="user_scroll_group",
                joinColumns = [JoinColumn(name = "id", referencedColumnName = "user_id" )],
                inverseJoinColumns = [JoinColumn(name = "id", referencedColumnName = "group_id")])
        val scrollGroup: ScrollGroup,
        @ManyToMany
        @JoinTable(name="guild_membership",
                joinColumns = [JoinColumn(name = "id", referencedColumnName = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "id", referencedColumnName = "guild_id")])
        val guilds: Set<Guild>
)