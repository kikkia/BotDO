package com.bot.db.entities

import com.bot.models.WarDay
import java.time.DayOfWeek
import javax.persistence.*

@Entity
@Table(name = "text_channel")
data class TextChannelEntity(
        @Id
        val id: String,
        @Column(name = "guild")
        val guild: String,
        var name: String,
        var announcement: Boolean,
        @Column(name="persistent_scrolls")
        var persistentScrolls: Boolean) {
        @Column(name = "war_channel")
        var warChannel: Int? = null

        fun getWarDay() : WarDay? {
                if (warChannel == null) {
                        return null
                } else {
                        return WarDay.getDayFromBitwise(warChannel!!)
                }
        }

        fun setWarDay(day: DayOfWeek) {
                warChannel = WarDay.getFromDayOfWeek(day)!!.id
        }
}