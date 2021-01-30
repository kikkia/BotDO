package com.bot.utils

object FamilyNameUtils {
    fun shouldChangeName(name: String, family: String): Boolean {
        return !(name.equals(family, ignoreCase = true) || name.contains("($family)"))
    }

    fun getFamilyInjectedName(name: String, family: String): String {
        return "$name($family)"
    }
}