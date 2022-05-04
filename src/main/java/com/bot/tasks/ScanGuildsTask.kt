package com.bot.tasks

import com.bot.models.BdoFamily
import com.bot.models.Region
import com.bot.service.BdoGuildService
import com.bot.service.FamilyService
import com.bot.service.MetricsService
import com.bot.utils.GuildScrapeUtils
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors
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
        var guildCount = 0
        var familyCount = 0
        // Website now requires exact name to search, so we go through those we have seen.
        var pageSize = 100
        var offset = 0
        var guildPage = bdoGuildService.getAllByRegion(region, pageSize, offset)
        val total = guildPage.totalElements
        while (offset < total) {
            for (guild in guildPage.content) {
                if (guild.last_scan.toInstant().isAfter(Instant.now().minusSeconds(3600 * 18))) {
                    continue
                }
                bdoGuildService.setScan(guild)
                var families: Set<BdoFamily>
                try {
                    families = GuildScrapeUtils.getGuildFamilies(guild.name, region)
                } catch (e: Exception) {
                    log.warn("Hit unexpected error when getting guild members", e)
                    continue
                }

                var familyNamesFromPage = families.stream().map { it.name }.collect(Collectors.toList())
                var familiesFromDB = bdoGuildService.getAllFamilyNamesInGuild(guild)

                for (fam in families) {
                    val famOpt = familyService.getFamily(fam.name, region, true)
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
                        familyService.removeFromGuild(familyService.getFamily(toRemove, region, false).get())
                    } catch (e: Exception) {
                        log.warn("failed to remove member $toRemove from guild ${guild.name}")
                    }
                }
                guildCount++
            }
            offset += pageSize
            guildPage = bdoGuildService.getAllByRegion(region, pageSize, offset)
        }

        log.info("Scan complete. Pages: $page | Guilds: $guildCount | Families: $familyCount")
        val duration = Duration.between(start, Instant.now())
        log.info("Total elapsed time: ${duration.toHoursPart()} Hours, ${duration.toMinutesPart()} Mins, ${duration.toSecondsPart()} Secs")
        log.info("rate = ${guildCount/duration.toSeconds()} guilds/sec and ${familyCount/duration.toSeconds()} fams/sec")
    }
}