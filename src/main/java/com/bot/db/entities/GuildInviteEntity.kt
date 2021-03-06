package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "guild_invite")
data class GuildInviteEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @Column(name = "code")
        var code: String,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        var guild: GuildEntity,
        @Column
        var uses: Int,
        @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
        @JoinColumn(name = "guild_invite_id")
        var roles: List<InviteRoleEntity>) {

        @Column(name = "welcome_message")
        var welcomeMessage: String? = null

        @Column(name = "guild_prefix")
        var guildPrefix: String? = null
}