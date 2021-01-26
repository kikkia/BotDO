package com.bot.models

import com.bot.db.entities.User
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

class ScrollHistory {
    val id: Int
    var user: User
        private set
    private var scrolls: MutableMap<Scroll, Int>
    var created: Timestamp

    constructor(user: User, scrolls: MutableMap<Scroll, Int>) {
        this.id = 0
        this.user = user
        this.scrolls = scrolls
        this.created = Timestamp.from(Instant.now())
    }

    constructor(user: User) : this(user, EnumMap(com.bot.models.Scroll::class.java)) {
    }

    constructor(id: Int, user: User, scrolls: MutableMap<Scroll, Int>, created: Timestamp) {
        this.id = id
        this.user = user
        this.scrolls = scrolls
        this.created = created
    }

    fun getScrolls(): Map<Scroll, Int> {
        return scrolls
    }

    fun getScrollCount(scroll: Scroll) : Int {
        return scrolls.getOrDefault(scroll, 0)
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

    /**
     * Outputs the scroll history in a discord friendly format
     */
    fun toMessage() : String {
        val completedScrolls = scrolls.entries.stream()
                .filter { it.value != 0 }
                .collect(Collectors.toList())
        var toReturn = "Scrolls completed on $created:\n"
        for (scroll in completedScrolls) {
            toReturn += "${scroll.key.displayName}: ${scroll.value}\n"
        }
        return toReturn
    }
}