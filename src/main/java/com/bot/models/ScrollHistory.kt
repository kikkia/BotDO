package com.bot.models

import com.bot.db.entities.User
import java.sql.Timestamp
import java.time.Instant

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
}