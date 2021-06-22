package com.bot.db.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "recruitment_bell")
data class RecruitBellEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @JoinColumn(name = "author_id", referencedColumnName = "id")
        @ManyToOne(fetch = FetchType.EAGER, optional = false)
        var author: UserEntity,
        @JoinColumn(name = "guild_id", referencedColumnName = "id")
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        var guild: GuildEntity
) {
    @Column(name = "created")
    val created: Timestamp? = null
}