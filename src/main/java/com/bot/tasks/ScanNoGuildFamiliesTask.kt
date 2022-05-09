package com.bot.tasks

import com.bot.models.Region
import com.bot.service.FamilyService
import com.bot.service.MetricsService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors

class ScanNoGuildFamiliesTask(private val familyService: FamilyService,
                              private val metricsService: MetricsService,
                              private val region: Region) : Thread() {

    override fun run() {
        val log = LoggerFactory.getLogger(this.javaClass.simpleName)
        val start = Instant.now()
        log.info("Starting family scan for: ${region.code}")

        // Get all public families not in a guild and not missing from site
        val pageSize = 100
        try {
            var familyPage = familyService.getFamiliesNotPrivateAndPresentInRegion(region.code, PageRequest.of(0, pageSize))
            while (familyPage.hasNext()) {
                // Filter the page down to just the entities not in a guild currently.
                // TODO: Try to do this at the ORM/Query layer
                val guildlessEntites = familyPage.content.stream()
                    .filter{fam ->
                        fam.memberships.stream()
                                .filter{ it.active }.findAny().isPresent
                    }.collect(Collectors.toList())

                for (family in guildlessEntites) {
                    // Sync the family from the bdo site to get info like new guilds/private
                    val opt = familyService.syncSingleFromSite(family.name, region)
                    metricsService.markFamilyUpdateExectution()

                    // Keep track of users marked as private / Missing from site
                    if (opt.isPresent && opt.get().private) {
                        metricsService.markFamilyUpdatePrivate()
                    }
                    if (opt.isEmpty) {
                        family.missingCount++
                        if (family.missingCount >= 5) {
                            family.missing = true
                            metricsService.markFamilyUpdateMissing()
                        }
                        familyService.save(family)
                    }
                }
                familyPage = familyService.getFamiliesNotPrivateAndPresentInRegion(region.code, familyPage.nextOrLastPageable())
            }
        } catch (e: Exception) {
            log.error("Failed to sync families", e)
        }
        log.info("Finished family Scan.")
        val duration = Duration.between(start, Instant.now())
        log.info("Total elapsed time: ${duration.toHoursPart()} Hours, ${duration.toMinutesPart()} Mins, ${duration.toSecondsPart()} Secs")
    }
}