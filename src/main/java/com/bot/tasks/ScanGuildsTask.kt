package com.bot.tasks

import com.bot.db.entities.BDOGuildEntity
import com.bot.models.BdoFamily
import com.bot.models.Region
import com.bot.service.BdoGuildService
import com.bot.service.FamilyService
import com.bot.service.MetricsService
import com.bot.utils.GuildScrapeUtils
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors
import java.util.stream.IntStream

class ScanGuildsTask(private val familyService: FamilyService,
                     private val bdoGuildService: BdoGuildService,
                     private val metricsService: MetricsService,
                     private val guildScrapeUtils: GuildScrapeUtils,
                     val region: Region) : Thread() {

    override fun run() {
        val log = LoggerFactory.getLogger(this.javaClass.simpleName)
        val start = Instant.now()
        log.info("Starting guild scan for: ${region.code}")

        // Scan entire list of guilds and sync them
        var page = 1
        var guildCount = 0
        var familyCount = 0
        // Website now requires exact name to search, so we go through those we have seen.
        var pageSize = 100
        var offset = 0
        try {
            var guildPage = bdoGuildService.getAllByRegion(region, PageRequest.of(0, pageSize))
            while (guildPage.hasNext()) {
                for (guild in guildPage.content) {
                    if (guild.last_scan.toInstant().isAfter(Instant.now().minusSeconds(3600 * 18))) {
                        continue
                    }
                    bdoGuildService.setScan(guild)
                    var families: Set<BdoFamily>
                    try {
                        families = guildScrapeUtils.getGuildFamilies(guild.name, region)
                    } catch (e: Exception) {
                        log.warn("Hit unexpected error when getting guild members", e)
                        continue
                    }

                    var familyNamesFromPage = families.stream().map { it.name }.collect(Collectors.toList())
                    var familiesFromDB = bdoGuildService.getAllFamilyNamesInGuild(guild)

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

                    // Find people to remove from guild
                    familiesFromDB.removeAll(familyNamesFromPage)
                    for (toRemove in familiesFromDB) {
                        try {
                            familyService.removeFromGuild(familyService.getFamily(toRemove, region, true).get())
                        } catch (e: Exception) {
                            log.warn("failed to remove member $toRemove from guild ${guild.name}")
                        }
                    }
                    guildCount++
                }
                offset += pageSize
                log.info("Scan Page $page finished")
                val duration = Duration.between(start, Instant.now())
                log.info("Total elapsed time: ${duration.toHoursPart()} Hours, ${duration.toMinutesPart()} Mins, ${duration.toSecondsPart()} Secs")
                log.info("rate = ${guildCount/duration.toSeconds()} guilds/sec and ${familyCount/duration.toSeconds()} fams/sec")
                page++
                guildPage = bdoGuildService.getAllByRegion(region, guildPage.nextOrLastPageable())
            }
        } catch( e: Exception) {
            log.error("Failed to scan guilds", e)
        } finally {
            log.info("Scan complete. Pages: $page | Guilds: $guildCount | Families: $familyCount")
            val duration = Duration.between(start, Instant.now())
            log.info("Total elapsed time: ${duration.toHoursPart()} Hours, ${duration.toMinutesPart()} Mins, ${duration.toSecondsPart()} Secs")
            log.info("rate = ${guildCount/duration.toSeconds()} guilds/sec and ${familyCount/duration.toSeconds()} fams/sec")
        }
    }
}