package com.bot.models

enum class BdoServerChannel(val display: String, val aliases: List<String>, val channelEndings : List<Int>) {
    BALENOS("Balenos", listOf("bal"), IntRange(1, 6).toList()),
    VALENCIA("Valencia", listOf("val"), IntRange(1, 6).toList()),
    VELIA("Velia", listOf("vel"), IntRange(1, 6).toList()),
    CALPHEON("Calpheon", listOf("cal", "calph"), IntRange(1, 6).toList()),
    SERENDIA("Serendia", listOf("ser"), IntRange(1,6).toList()),
    MEDIAH("Mediah", listOf("med"), IntRange(1,6).toList()),
    KAMASYLVIA("Kamasylvia", listOf("kama"), IntRange(1, 4).toList()),
    OLVIA("Olvia", listOf(), IntRange(1, 6).toList()),
    ARSHA("Arsha", listOf("pvp"), listOf());

    companion object {
        fun getServerFromName(name: String) : BdoServerChannel? {
            val cleanedName = name.trim().toLowerCase()
            for (c in values()) {
                if (c.aliases.contains(cleanedName) || c.display.toLowerCase() == cleanedName) {
                    return c
                }
            }
            return null
        }
    }
}