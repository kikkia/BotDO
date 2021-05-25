package com.bot.models

import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.*

enum class WarDay(val day: DayOfWeek, val id: Int) {
    SUNDAY(DayOfWeek.SUNDAY, 1),
    MONDAY(DayOfWeek.MONDAY, 2),
    TUESDAY(DayOfWeek.TUESDAY, 4),
    WEDNESDAY(DayOfWeek.WEDNESDAY, 8),
    THURSDAY(DayOfWeek.THURSDAY, 16),
    FRIDAY(DayOfWeek.FRIDAY, 32),
    SATURDAY(DayOfWeek.SATURDAY, 64);

    companion object {
        fun getAllDays(days: Int?) : List<WarDay> {
            if (days == null) {
                return mutableListOf()
            }

            val list = mutableListOf<WarDay>()
            for (day in values()) {
                if (days.and(day.id) > 0) {
                    list.add(day)
                }
            }
            return list
        }

        fun getBitWiseForDays(days: List<WarDay>) : Int {
            var sum = 0
            for (day in days) {
                sum += day.id
            }
            return sum
        }

        fun getFromTimestamp(time: Timestamp) : WarDay {
            return getFromDayOfWeek(time.toLocalDateTime().dayOfWeek)!!
        }

        fun getFromDate(time: Date) : WarDay {
            return getFromTimestamp(Timestamp.from(time.toInstant()))
        }

        fun getFromDayOfWeek(day: DayOfWeek) : WarDay? {
            for (warDay in values()) {
                if (warDay.day == day) {
                    return warDay
                }
            }
            return null
        }

        fun getDayFromBitwise(bitwise: Int) : WarDay? {
            for (day in values()) {
                if (day.id.and(bitwise) > 0) {
                    return day
                }
            }
            return null
        }

        fun getFromString(input: String) : WarDay? {
            for (day in values()) {
                if (day.name.equals(input, ignoreCase = true)) {
                    return day
                }
            }
            return null
        }
    }
}