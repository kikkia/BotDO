package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "invite_role")
data class InviteRoleEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @Column(name = "role_id")
        var roleId: String,
        @OneToOne
        @JoinColumn(name = "guild_invite_id")
        var invite: GuildInviteEntity
)