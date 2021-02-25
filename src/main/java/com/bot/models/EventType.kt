package com.bot.models

enum class EventType(val id: Int) {
    CUSTOM(1),
    BOSS(2),
    RESET(3);

    companion object {
        private val map = values().associateBy(EventType::id)
        fun fromInt(type: Int) = map[type]
    }
}