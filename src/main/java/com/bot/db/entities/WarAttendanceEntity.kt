package com.bot.db.entities

import java.sql.Timestamp
import java.time.Instant
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
    @Column(name = "maybe")
    var maybe: Boolean = false
    @Column(name = "created")
    val created: Timestamp = Timestamp.from(Instant.now())

    fun toMessageEntry() : String {
        val gear = user.gearset
        val ap = gear?.ap?.toString() ?: "?"
        val dp = gear?.dp?.toString() ?: "?"
        val aap = gear?.awkAp?.toString() ?: "?"
        val cl: String = gear?.getClassName() ?: "?"
        return "$ap/$aap/$dp - ${user.getEffectiveName()} - $cl"
    }
}