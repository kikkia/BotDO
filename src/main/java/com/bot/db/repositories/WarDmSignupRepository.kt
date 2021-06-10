package com.bot.db.repositories

import com.bot.db.entities.WarDmSignupEntity
import com.bot.db.entities.WarEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface WarDmSignupRepository: CrudRepository<WarDmSignupEntity, Int> {
    fun findByMessageId(messageId: String) : Optional<WarDmSignupEntity>
    fun findByMessageIdAndActiveTrue(messageId: String) : Optional<WarDmSignupEntity>
    fun findAllByWarEntityAndUserId(warEntity : WarEntity, userId: String) : List<WarDmSignupEntity>
    fun findAllByWarEntityAndActiveTrue(warEntity: WarEntity) : List<WarDmSignupEntity>
    fun findAllByActiveTrue() : List<WarDmSignupEntity>
}