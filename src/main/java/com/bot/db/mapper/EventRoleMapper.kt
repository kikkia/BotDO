package com.bot.db.mapper

import com.bot.db.entities.EventEntity
import com.bot.db.entities.EventRoleEntity

class EventRoleMapper {
    companion object {
        fun map(event: EventEntity, roleId: String): EventRoleEntity {
            return EventRoleEntity(event, roleId)
        }
    }
}