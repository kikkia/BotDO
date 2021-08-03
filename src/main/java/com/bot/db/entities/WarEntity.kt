package com.bot.db.entities

import com.bot.models.WarDay
import com.bot.models.WarNode
import java.sql.Timestamp
import java.time.Instant
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
        var messageId: String,
        @ManyToOne
        @JoinColumn(name = "text_channel")
        var channel: TextChannelEntity,
        @ManyToOne
        @JoinColumn(name = "guild_id")
        val guild: BDOGuildEntity) {
    @Column(name = "won")
    var won: Boolean? = false
    @Column(name = "node")
    var node: Int? = null
    @Column(name = "archived")
    var archived: Boolean = false
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "war", cascade = [CascadeType.ALL], orphanRemoval = true)
    val vods: MutableList<WarVodEntity> = mutableListOf()
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "war", cascade = [CascadeType.ALL], orphanRemoval = true)
    val stats: MutableList<WarStatsEntity> = mutableListOf()

    fun setNode(node: WarNode) {
        this.node = node.id
    }

    fun getWarNode(): WarNode? {
        return WarNode.getNodeFromId(node)
    }

    fun getWarDay(): WarDay {
        return WarDay.getFromTimestamp(warTime)
    }

    fun addVod(link: String, name: String) {
        vods.add(WarVodEntity(0, this, link, name))
    }

    fun addStat(link: String) {
        stats.add(WarStatsEntity(0, this, link))
    }

    fun getAverageGS(): Int {
        val attendeeList = attendees
                .filter { it.user.gearset != null }
                .filter { !it.notAttending }
                .filter { it.user.gearset!!.getGearScore() > 0 }
                .toList()

        if (attendeeList.isEmpty()) {
            return 0
        }

        val gearscores = attendeeList.asSequence()
                .map { it.user.gearset!!.getGearScore() }
                .toList()
        var totalGS = 0
        for (att in gearscores) {
            totalGS += att
        }
        return totalGS / gearscores.size
    }

    fun past(): Boolean {
        return warTime.before(Timestamp.from(Instant.now()))
    }
}