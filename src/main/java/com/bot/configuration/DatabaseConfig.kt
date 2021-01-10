package com.bot.configuration

import com.bot.configuration.properties.DatabaseProperties
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean

open class DatabaseConfig {
    @Bean
    open fun hikariDataSource(config: HikariConfig?): HikariDataSource {
        return HikariDataSource(config)
    }

    @Bean
    open fun hikariConfig(properties: DatabaseProperties): HikariConfig {
        if (properties.uri.isBlank()) {
            throw RuntimeException("DB URI must be provided")
        }

        val config = HikariConfig()
        config.password = properties.password
        config.username = properties.username
        config.jdbcUrl = "jdbc:mysql://" + properties.uri + "/" + properties.schema
        config.schema = properties.schema
        config.maximumPoolSize = 10
        config.minimumIdle = 2
        return config
    }
}