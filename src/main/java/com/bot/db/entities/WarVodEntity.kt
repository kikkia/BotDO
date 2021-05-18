package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "war_vod")
class WarVodEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "war_id")
        val war: WarEntity,
        @Column(name = "vod_link")
        val vodLink: String
) {
    @Column(name = "type")
    var type: String? = null
}