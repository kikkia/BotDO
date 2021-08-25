package com.bot.api.models

data class War(
    val warId: Int,
    val warTime: Long,
    val node: String,
    val win: Boolean,
    val attendees: List<WarAttendance>,
    val archived: Boolean
)