package com.bot.db.entities

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
        @OneToMany(fetch = FetchType.EAGER, mappedBy = "war", cascade = [CascadeType.ALL])
        val attendees: List<WarAttendanceEntity>,
        @Column(name = "messageId")
        val messageId: String) {
    @Column(name = "won")
    var won: Boolean? = null
    @Column(name = "node")
    var node: Int? = null

    fun setNode(node: WarNode) {
        this.node = node.id
    }

    fun getWarNode(): WarNode? {
        return WarNode.getNodeFromId(node)
    }
}