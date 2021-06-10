package com.bot.configuration

import com.bot.configuration.properties.MetricsProperties
import com.timgroup.statsd.NonBlockingStatsDClient
import com.timgroup.statsd.StatsDClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class MetricsConfiguration {
    @Bean
    open fun dataDogStatsDClient(properties: MetricsProperties): StatsDClient {
        return NonBlockingStatsDClient(
                properties.prefix,
                properties.hostname,
                8125,
                "bdo:main")
    }
}