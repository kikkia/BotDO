package com.bot.api.models.mappers

import com.bot.api.models.War
import com.bot.db.entities.WarEntity
import com.bot.models.WarNode
import java.util.stream.Collectors

class WarMapper {
    companion object {
        fun map(war: WarEntity) : War {
            val nodeName = WarNode.getNodeFromId(war.node).toString()
            val won = war.won == true
            return War(war.id,
                war.warTime.time,
                nodeName,
                won,
                war.attendees.stream().map { WarAttendeeMapper.map(it) }.collect(Collectors.toList()),
                war.archived)
        }
    }
}