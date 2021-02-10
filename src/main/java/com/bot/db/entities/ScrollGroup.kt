package com.bot.db.entities

import com.bot.db.mapper.ScrollInventoryMapper
import com.bot.models.ScrollInventory
import java.util.stream.Collectors
import javax.persistence.*

@Entity
@Table(name = "scroll_group")
data class ScrollGroup(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        var guild: Guild,
        var name: String,
        @OneToMany(cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE
        ], fetch = FetchType.EAGER)
        @JoinTable(name = "user_scroll_group",
                joinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
        var users: Set<User>) {

        fun toMessage() : String {
                var message = "Overview for `$name`\n"
                if (users.isNotEmpty()) {
                        message += "`Users in group`\n```"
                        message += users.stream()
                                .map { it.getEffectiveName() }
                                .collect(Collectors.toList()).toString()
                } else {
                        return "${message}This group has no members."
                }

                message += "```\n"
                // Construct a composite scroll inventory for the group
                val compositeInventory = ScrollInventory(User(name, name))

                users.stream().map { ScrollInventoryMapper.map(it.inventory!!).getScrolls() }
                        .collect(Collectors.toList()).forEach { map ->
                                map.forEach {
                                        compositeInventory.addScroll(it.key, it.value)
                                }
                        }

                return message + compositeInventory.toMessage()
        }
}