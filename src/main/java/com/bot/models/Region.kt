package com.bot.models

enum class Region(val code: String, val indexDate: String) {
    NORTH_AMERICA("NA", "Apr 6, 2021"),
    SOUTH_AMERICA("SA", "N/A"),
    EUROPE("EU", "Nov 11, 2021"),
    KOREA("KR", "N/A"),
    RUSSIA("RU", "N/A"),
    OCEANA("OCE", "N/A"),
    TURKEY("TR", "N/A"),
    JAPAN("JP", "N/A"),
    SOUTHEAST_ASIA("SEA", "N/A"),
    CONSOLE("CONSOLE", "N/A");

    companion object {
        fun getByCode(code: String): Region? {
            return values().find { it.code.equals(code, true) }
        }
    }
}