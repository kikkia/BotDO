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
                joinColumns = [JoinColumn(name = "id", referencedColumnName = "guild_id")],
                inverseJoinColumns = [JoinColumn(name = "id", referencedColumnName = "user_id")])
        val users: Set<User>
)