package com.bot.api.models.mappers

import com.bot.api.models.ApiUser
import com.bot.db.entities.UserEntity

class ApiUserMapper {
    companion object {
        fun map(entity: UserEntity) : ApiUser {
            val className = if (entity.gearset != null) entity.gearset!!.getClassName()
                else "Unknown"
            return ApiUser(entity.id, entity.getEffectiveName(), className)
        }
    }
}