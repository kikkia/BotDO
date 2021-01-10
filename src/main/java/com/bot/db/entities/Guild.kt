package com.bot.db.entities

import javax.persistence.*

@Entity
data class Guild(
        @Id
        val id: String,
        @Column(nullable = false)
        val name: String,
        @ManyToMany
        @JoinTable(name = "guild_membership",
                joinColumns = [JoinColumn(name = "guild_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
        val users: Set<User>
)