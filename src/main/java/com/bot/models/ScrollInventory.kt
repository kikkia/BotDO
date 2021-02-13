package com.bot.models

import com.bot.db.entities.User
import java.lang.IllegalArgumentException
import java.util.*
import java.util.stream.Collectors

class ScrollInventory {
    val id: Int
    var user: User
        private set
    private var scrolls: MutableMap<Scroll, Int>

    constructor(user: User) {
        this.id = 0
        this.user = user
        scrolls = EnumMap(Scroll::class.java)
    }

    constructor(id: Int, user: User, map: MutableMap<Scroll, Int>) {
        this.id = id
        this.user = user
        scrolls = map
    }

    /**
     * Adds the count to the existing scroll count
     *
     * @param scroll - The type of scroll
     * @param count -  The count of scrolls to add
     */
    fun addScroll(scroll: Scroll, count: Int) {
        val current = getScrollCount(scroll)
        scrolls[scroll] = current + count
    }

    /**
     * Remove scrolls from the existing scroll counts
     *
     * @param scroll - The type of scroll
     * @param count -  The count of scrolls to remove
     */
    fun removeScroll(scroll: Scroll, count: Int) {
        val current = getScrollCount(scroll)

        if (current < count) {
            throw IllegalArgumentException("You cannot remove more scrolls than you have.")
        }

        scrolls[scroll] = current - count
    }

    /**
     * Puts the count for that scroll overwriting any existing values
     *
     * @param scroll - The type of scroll
     * @param count - The count of scrolls
     */
    fun putScroll(scroll: Scroll, count: Int) {
        scrolls[scroll] = count
    }

    fun getScrolls(): Map<Scroll, Int> {
        return scrolls
    }

    fun getScrollCount(scroll: Scroll) : Int {
        return scrolls.getOrDefault(scroll, 0)
    }

    fun toMessage() : String {
        val obtainedScrolls = scrolls.entries.stream()
                .filter { it.value != 0 }
                .collect(Collectors.toList())
        if (obtainedScrolls.size < 1) {
            return "`${user.familyName} does not have any scrolls`"
        }
        var message = "`Scroll inventory for ${user.getEffectiveName()}`\n```"
        for (scroll in obtainedScrolls) {
            message += "${scroll.key.displayName}: ${scroll.value}\n"
        }
        return "$message```"
    }
}