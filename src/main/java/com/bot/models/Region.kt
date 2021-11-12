package com.bot.models

enum class Region(val code: String) {
    NORTH_AMERICA("NA"),
    SOUTH_AMERICA("SA"),
    EUROPE("EU"),
    KOREA("KR"),
    RUSSIA("RU"),
    OCEANA("OCE"),
    TURKEY("TR"),
    JAPAN("JP"),
    SOUTHEAST_ASIA("SEA"),
    CONSOLE("CONSOLE");

    companion object {
        fun getByCode(code: String): Region? {
            return values().find { it.code.equals(code, true) }
        }
    }
}