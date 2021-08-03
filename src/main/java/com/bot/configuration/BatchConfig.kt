package com.bot.configuration

import com.bot.batch.processors.MaybeReminderProcessor
import com.bot.batch.readers.WarReader
import com.bot.batch.writers.WarReminderWriter
import com.bot.db.entities.WarEntity
import com.bot.models.WarReminder
import com.bot.service.DiscordService
import com.bot.service.WarService
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.PlatformTransactionManager

import java.util.*
import javax.sql.DataSource


@Configuration
@EnableScheduling
@EnableBatchProcessing
open class BatchConfig(val jobBuilderFactory: JobBuilderFactory,
                  val stepBuilderFactory: StepBuilderFactory,
                  val transactionManagerBean: PlatformTransactionManager,
                  val discordService: DiscordService,
                  val warService: WarService) : DefaultBatchConfigurer() {

    val runIdIncrementer = RunIdIncrementer()

    fun warReader(): ItemReader<List<WarEntity>> {
        return WarReader(warService)
    }

    fun reminderWriter(): ItemWriter<List<WarReminder>> {
        return WarReminderWriter(discordService)
    }

    fun maybeReminderStep(): Step {
        return stepBuilderFactory.get("maybeReminderStep")
            .chunk<List<WarEntity>, List<WarReminder>>(1)
            .reader(warReader())
            .processor(MaybeReminderProcessor())
            .writer(reminderWriter())
            .build()
    }

    fun maybeReminderJob(): Job {
        return jobBuilderFactory.get("remindMaybe")
            .incrementer(runIdIncrementer)
            .start(maybeReminderStep())
            .build()
    }

    //@Scheduled(cron = "0 0 18 * * *", zone = "America/Chicago")
    fun launchMaybeReminder() {
        jobLauncher.run(maybeReminderJob(),
            JobParametersBuilder().addDate("date", Date()).toJobParameters())
    }

    override fun setDataSource(dataSource: DataSource?) {
        // override to do not set datasource even if a datasource exist.
        // initialize will use a Map based JobRepository (instead of database)
    }

    override fun getTransactionManager(): PlatformTransactionManager {
        return transactionManagerBean
    }

    // TODO: Look in to porting cleanable repo from Vinny-rss
}