package com.bot.db.entities

import java.sql.Timestamp
import java.time.Instant
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
        @Column(name = "max_uses")
        var maxUses: Int?,
        @Column(name = "max_age")
        var maxAge : Int?,
        @OneToMany(fetch = FetchType.EAGER, mappedBy = "guildInvite", cascade=[CascadeType.ALL])
        var roles: List<InviteRoleEntity>,
        @Column(name = "author")
        var author: String?,
        @Column(name = "created")
        var created: Timestamp?) {

        @Column(name = "welcome_message")
        var welcomeMessage: String? = null

        @Column(name = "guild_prefix")
        var guildPrefix: String? = null

        // Max age is overridden to be more null safe
        private val safeMaxAge: Int
                get() = if (maxAge == null) {
                        0
                } else {
                        maxAge!!
                }

        fun isExpired() : Boolean {
                return safeMaxAge > 0 && Instant.now().isAfter(
                        Instant.ofEpochSecond(((created?.time?.div(1000) ?: 0)
                                + safeMaxAge)))
        }
}