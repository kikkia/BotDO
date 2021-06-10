package com.bot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "metrics")
open class MetricsProperties {
    var hostname = "localhost"
    var prefix = "bdo"
}