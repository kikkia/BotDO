package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "scroll_group")
data class ScrollGroup(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        var guild: Guild,
        var name: String,
        @OneToMany(cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE
        ])
        @JoinTable(name = "user_scroll_group",
                joinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
        var users: Set<User>
)