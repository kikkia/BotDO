package com.bot.service

import com.timgroup.statsd.StatsDClient
import org.springframework.stereotype.Service

@Service
open class MetricsService(private val client: StatsDClient) {
    open fun markFamilyUpdateExectution() {
        client.incrementCounter("bdo.family.update")
    }

    open fun markFamilyUpdatePrivate() {
        client.incrementCounter("bdo.family.privated")
    }

    open fun markFamilyUpdateMissing() {
        client.incrementCounter("bdo.family.missing")
    }

    open fun markGuildUpdateExcecution() {
        client.incrementCounter("bdo.guild.update")
    }

    private fun markCommandExecution(commandName: String, outcome:String) {
        val commandNameTag = "command:$commandName"
        val outcomeTag = "outcome:$outcome"
        client.incrementCounter("bdo.command", commandNameTag, outcomeTag)
    }

    open fun markCommandSuccess(commandName: String) {
        markCommandExecution(commandName, "success")
    }

    open fun markCommandFailure(commandName: String) {
        markCommandExecution(commandName, "failure")
    }
}
