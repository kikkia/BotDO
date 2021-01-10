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
        val guild: Guild,
        val name: String,
        @OneToMany
        @JoinTable(name = "user_scroll_group",
                joinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
        val users: Set<User>
)