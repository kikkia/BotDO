package com.bot.db.entities

import com.bot.models.WarDay
import com.bot.models.WarNode
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "war")
class WarEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        @Column(name = "war_time")
        val warTime: Timestamp,
        @OneToMany(fetch = FetchType.EAGER, mappedBy = "war", cascade = [CascadeType.ALL], orphanRemoval = true)
        var attendees: List<WarAttendanceEntity>,
        @Column(name = "message_id")
        val messageId: String,
        @ManyToOne
        @JoinColumn(name = "text_channel")
        val channel: TextChannel,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        val guild: BDOGuildEntity) {
    @Column(name = "won")
    var won: Boolean? = null
    @Column(name = "node")
    var node: Int? = null
    @Column(name = "archived")
    var archived: Boolean = false

    fun setNode(node: WarNode) {
        this.node = node.id
    }

    fun getWarNode(): WarNode? {
        return WarNode.getNodeFromId(node)
    }

    fun getWarDay(): WarDay {
        return WarDay.getFromTimestamp(warTime)
    }

    fun getAverageGS(): Int {
        if (attendees.isEmpty()) {
            return 0
        }
        var totalGS = 0
        for (att in attendees) {
            var userGS = 0
            if (att.user.gearset != null) {
                userGS = att.user.gearset!!.getGearScore()
            }
            totalGS += userGS
        }
        return totalGS / attendees.size
    }
}