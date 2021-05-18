package com.bot.db.entities

import javax.persistence.*

@Entity
@Table(name = "war_attendance")
class WarAttendanceEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "war_id")
        val war: WarEntity,
        @ManyToOne
        @JoinColumn(name = "user_id")
        val user: UserEntity) {
    @Column(name = "tardy")
    var tardy: Boolean = false
    @Column(name = "no_show")
    var noShow: Boolean = false
}