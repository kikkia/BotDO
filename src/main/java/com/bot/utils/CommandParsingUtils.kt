package com.bot.utils

import java.lang.IllegalArgumentException
import com.bot.models.Scroll
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object CommandParsingUtils {
    @Throws(IllegalArgumentException::class)
    fun parseScrollUpdates(input: String): List<Pair<Scroll, Int>> {
        val scrolls = input.split(", ").toTypedArray()

        // Scroll pair denotes a pair of scroll/number
        val scrollPairs: MutableList<Pair<Scroll, Int>> = ArrayList()
        for (scrollPair in scrolls) {
            val parts = scrollPair.split(" ").toTypedArray()
            require(parts.size == 2) {
                "Error found near `" +
                        scrollPair + "` More than 2 parts found."
            }
            // Find the scroll name and the quantity
            // TODO: Custom exceptions
            var quantity: Int
            var scroll: Scroll?
            if (StringUtils.isNumeric(parts[0])) {
                quantity = parts[0].toInt()
                scroll = Scroll.getScrollForName(parts[1])
            } else if (StringUtils.isNumeric(parts[1])) {
                quantity = parts[1].toInt()
                scroll = Scroll.getScrollForName(parts[0])
            } else {
                throw IllegalArgumentException("Error found near `" +
                        scrollPair + "` No quantity found.")
            }
            requireNotNull(scroll) {
                "Error found near `" +
                        scrollPair + "` No valid scroll name found."
            }
            scrollPairs.add(Pair(scroll, quantity))
        }
        return scrollPairs
    }

    fun parseArgsToDate(input: String, warStartTime: String) : Date? {
        val args = input.split(" ")
        val formatter = SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("America/Chicago")
        var date: Date? = null
        for (arg in args) {
            val timeToParse = "$arg $warStartTime"
            try {
                date = formatter.parse(timeToParse)
                break
            } catch (e: ParseException) {
                continue
            }
        }
        return date
    }
}