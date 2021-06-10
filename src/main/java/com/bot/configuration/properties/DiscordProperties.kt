package com.bot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "discord")
open class DiscordProperties {
    var token = ""
    var owner = ""
    var shards = 1
    var startShard = 0
    var endShard = 0
    var scanFamilies = true
}