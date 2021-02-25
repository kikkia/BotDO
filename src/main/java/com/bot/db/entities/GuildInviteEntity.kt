package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "guild_invite")
data class GuildInviteEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        var guild: GuildEntity,
        @Column
        var uses: Int
)