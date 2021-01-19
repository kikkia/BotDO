package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "guild")
data class Guild(
        @Id
        var id: String,
        @Column(nullable = false)
        var name: String,
        @ManyToMany(cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE
        ], fetch = FetchType.EAGER)
        @JoinTable(name = "guild_membership",
                joinColumns = [JoinColumn(name = "guild_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
        var users: Set<User>
)