package com.bot.tasks

import com.bot.models.BdoFamily
import com.bot.models.Region
import com.bot.service.BdoGuildService
import com.bot.service.FamilyService
import com.bot.service.MetricsService
import com.bot.utils.GuildScrapeUtils
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.stream.IntStream

class ScanGuildsTask(private val familyService: FamilyService,
                     private val bdoGuildService: BdoGuildService,
                     private val metricsService: MetricsService,
                     val region: Region) : Thread() {

    override fun run() {
        val log = LoggerFactory.getLogger(this.javaClass.simpleName)
        val start = Instant.now()
        log.info("Starting guild scan for: ${region.code}")

        // Scan entire list of guilds and sync them
        var page = 1
        var pageCount = 0
        var guildCount = 0
        var familyCount = 0
        for (i in IntStream.range('a'.toInt(), 'z'.toInt())) {
            page = 1
            while(true) {
                try {
                    val guildNames = GuildScrapeUtils.getGuildNamesOnPage(page, i.toChar().toString())
                    if (guildNames.isEmpty()) {
                        break
                    }

                    for (name in guildNames) {
                        var families: Set<BdoFamily>
                        try {
                            families = GuildScrapeUtils.getGuildFamilies(name, region)
                        } catch (e: Exception) {
                            log.warn("Hit unexpected error when getting guild members", e)
                            continue
                        }

                        val guildOpt = bdoGuildService.getByNameAndRegion(name, region)
                        val guild = if (guildOpt.isEmpty) {
                            bdoGuildService.createNewGuild(name, region)
                        } else {
                            if (guildOpt.get().last_scan.toInstant().isAfter(Instant.now().minusSeconds(3600 * 12))) {
                                continue
                            }
                            bdoGuildService.setScan(guildOpt.get())
                        }

                        for (fam in families) {
                            val famOpt = familyService.getFamily(fam.name, region, false)
                            val family = if (famOpt.isEmpty) {
                                familyService.createMinimal(fam.name, fam.id, region)
                            } else {
                                famOpt.get()
                            }
                            familyService.addToGuild(family, guild)

                            // These Ids can potentially change
                            if (family.externalId != fam.id) {
                                familyService.updateExternalId(family, fam.id)
                            }
                            metricsService.markFamilyUpdateExectution()
                            familyCount++
                        }
                        metricsService.markGuildUpdateExcecution()
                        guildCount++
                    }
                    if (page % 50 == 0) {
                        log.info("Guild sync progress.. Page: $page | Guilds: $guildCount | Families: $familyCount")
                    }
                    page++
                } catch (e: Exception) {
                    log.error("Sync Job Step failed due to exception", e)
                    log.warn("Sync job step for ${i.toChar()} failed after $page pages, $guildCount guilds and $familyCount families")
                    continue
                }
            }
            pageCount += page
        }
        log.info("Scan complete. Pages: $page | Guilds: $guildCount | Families: $familyCount")
        val duration = Duration.between(start, Instant.now())
        log.info("Total elapsed time: ${duration.toHoursPart()} Hours, ${duration.toMinutesPart()} Mins, ${duration.toSecondsPart()} Secs")
    }
}