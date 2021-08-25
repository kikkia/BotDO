package com.bot.api.models.mappers

import com.bot.api.models.WarAttendance
import com.bot.db.entities.WarAttendanceEntity

class WarAttendeeMapper {
    companion object {
        fun map(entity: WarAttendanceEntity) : WarAttendance {
            return WarAttendance(entity.war.id,
                entity.user.id,
                entity.attended,
                entity.maybe,
                !entity.maybe && !entity.notAttending
            )
        }
    }
}