package com.bot.models

import com.bot.db.entities.WarEntity

data class WarReminder(val guild: String, val user: String, val war: WarEntity, val message: String)