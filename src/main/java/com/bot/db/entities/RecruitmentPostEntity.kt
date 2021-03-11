package com.bot.db.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "recruitment_post")
data class RecruitmentPostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    var author: UserEntity,
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var guild: GuildEntity,
    @Column(name = "channel")
    var channel: String,
    @Column(name = "created")
    val timestamp: Timestamp
)