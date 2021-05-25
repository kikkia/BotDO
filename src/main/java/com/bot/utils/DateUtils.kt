package com.bot.utils

import com.bot.models.WarDay
import java.util.*

class DateUtils {
    companion object {
        fun getNextWarDate(day: WarDay) : Date {
            val cal = Calendar.getInstance()
            val dayOfWeek = day.day.value + 1 // Offsets calendar -> DayOfWeek diff
            val hour = 20
            val minute = 0

            if (cal[Calendar.DAY_OF_WEEK]-1 != dayOfWeek) {
                cal.add(Calendar.DAY_OF_MONTH, (dayOfWeek + 7 - cal[Calendar.DAY_OF_WEEK]) % 7)
            } else {
                val minOfDay = cal[Calendar.HOUR_OF_DAY] * 60 + cal[Calendar.MINUTE]
                if (minOfDay >= hour * 60 + minute) cal.add(Calendar.DAY_OF_MONTH, 7) // Bump to next week
            }
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal[Calendar.MINUTE] = minute
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            return Date.from(cal.toInstant())
        }
    }
}