package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "scroll_group")
data class ScrollGroup(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        val guild: Guild,
        val name: String,
        @OneToMany
        @JoinTable(name = "user_scroll_group",
                joinColumns = [JoinColumn(name = "id", referencedColumnName = "scroll_group_id")],
                inverseJoinColumns = [JoinColumn(name = "id", referencedColumnName = "user_id")])
        val users: Set<User>
)