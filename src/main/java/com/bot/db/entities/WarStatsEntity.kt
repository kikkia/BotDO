package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "war_stats")
class WarStatsEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "war_id")
        val war: WarEntity,
        @Column(name = "img_link")
        val imgLink: String
) {}