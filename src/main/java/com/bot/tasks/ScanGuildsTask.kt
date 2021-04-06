package com.bot.tasks

import com.bot.models.BdoFamilyId
import com.bot.models.Region
import com.bot.service.BdoGuildService
import com.bot.service.FamilyService
import com.bot.utils.GuildScrapeUtils
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

class ScanGuildsTask(private val familyService: FamilyService,
                     private val bdoGuildService: BdoGuildService,
                     val region: Region) : Thread() {

    override fun run() {
        val log = LoggerFactory.getLogger(this.javaClass.simpleName)
        val start = Instant.now()
        log.info("Starting guild scan for: ${region.code}")

        // Scan entire list of guilds and sync them
        var page = 1
        var guildCount = 0
        var familyCount = 0
        while(true) {
            try {
                val guildNames = GuildScrapeUtils.getGuildNamesOnPage(page)
                if (guildNames.isEmpty()) {
                    break
                }

                for (name in guildNames) {
                    var families: Set<BdoFamilyId>
                    try {
                        families = GuildScrapeUtils.getGuildFamilies(name, region)
                    } catch (e : Exception) {
                        log.warn("Hit unexpected error when getting guild members", e)
                        continue
                    }

                    val guildOpt = bdoGuildService.getByNameAndRegion(name, region)
                    val guild = if (guildOpt.isEmpty) {
                        bdoGuildService.createNewGuild(name, region)
                    } else {
                        guildOpt.get()
                    }

                    for (fam in families) {
                        val famOpt = familyService.getFamily(fam.name, region)
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
                        familyCount++
                    }
                    guildCount++
                }
                if (page % 50 == 0) {
                    log.info("Guild sync progress.. Page: $page | Guilds: $guildCount | Families: $familyCount")
                }
                page++
            } catch (e: Exception) {
                log.error("Sync Job failed due to exception", e)
                log.warn("Sync job failed after $page pages, $guildCount guilds and $familyCount families")
            }
        }
        log.info("Scan complete. Pages: $page | Guilds: $guildCount | Families: $familyCount")
        val duration = Duration.between(start, Instant.now())
        log.info("Total elapsed time: ${duration.toHoursPart()} Hours, ${duration.toMinutesPart()} Mins, ${duration.toSecondsPart()} Secs")
    }
}