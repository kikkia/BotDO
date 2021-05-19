package com.bot.db.entities

import net.dv8tion.jda.api.entities.Guild
import javax.persistence.*

@Entity
@Table(name = "guild")
data class GuildEntity(
        @Id
        var id: String,
        @Column(nullable = false)
        var name: String,
        @Column(name = "sync_names")
        var syncNames: Boolean,
        @ManyToMany(cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE
        ], fetch = FetchType.EAGER)
        @JoinTable(name = "guild_membership",
                joinColumns = [JoinColumn(name = "guild_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
        var users: Set<UserEntity>
) {
        @Column(name = "recruit_message")
        var recruitMessage: String? = null
        @Column(name = "welcome_channel")
        var welcomeChannel: String? = null
        @Column(name = "rules_channel")
        var entryChannel: String? = null
        @Column(name = "recruit_role")
        var recruitRole: String? = null
        @Column(name = "log_channel")
        var logChannel: String? = null
        @OneToOne
        @JoinColumn(name = "bdo_guild_id")
        var bdoGuild: BDOGuildEntity? = null

        fun hasDefaultInviteChannel() : Boolean {
                return entryChannel != null
        }

        fun hasDefaultRecruitRole() : Boolean {
                return recruitRole != null
        }

        fun hasDefaultRecruitMessage() : Boolean {
                return recruitMessage != null
        }

        fun hasWelcomeMessageChannel() : Boolean {
                return welcomeChannel != null
        }

        companion object {

                // Helper to get a partial from a discord guild entity
                fun partialFrom(guild: Guild) : com.bot.db.entities.GuildEntity {
                        return GuildEntity(guild.id, guild.name, false, emptySet())
                }
        }
}