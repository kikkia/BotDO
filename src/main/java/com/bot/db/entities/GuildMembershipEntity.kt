package com.bot.db.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "guild_family_membership")
class GuildMembershipEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @ManyToOne
    @JoinColumn(name = "family")
    val family: FamilyEntity,
    @ManyToOne
    @JoinColumn(name = "guild")
    val guild: BDOGuildEntity,
    @Column(name = "created")
    val created: Timestamp,
    @Column(name = "active")
    var active: Boolean
) {
    @Column(name = "removed")
    var removed: Timestamp? = null
}