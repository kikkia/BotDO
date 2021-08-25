package com.bot.api.models

data class WarAttendance(
    val warId: Int,
    val userId: String,
    val userDisplayName: String,
    val attended: Boolean,
    val maybe: Boolean,
    val yes: Boolean)