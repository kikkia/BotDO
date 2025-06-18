package com.bot.commands.user

import com.bot.db.entities.GearsetEntity
import com.bot.db.entities.UserEntity
import com.bot.exceptions.InvalidGearFieldException
import com.bot.models.GearArgumentTag
import com.bot.service.UserService
import com.bot.utils.FormattingUtils
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Component

@Component
class GearCommand(val userService: UserService): Command() {

    init {
      this.name = "gear"
    }

    override fun execute(p0: CommandEvent?) {
        val user: UserEntity
        // This will display gear for author or mentioned user
        if (p0!!.message.mentions.users.isNotEmpty() ||
                (p0.args.isEmpty() && p0.message.attachments.isEmpty())) {
            // Get gear for mentioned users
            val userId = if (p0.args.isBlank()) p0.author.id else p0.message.mentions.users[0].id
            user = userService.getById(userId)
            val gear = user.gearset
            if (gear == null) {
                p0.replyWarning("I can't find any gear info for ${user.name}")
            } else {
                p0.reply(buildEmbedForGear(user, gear))
            }
            return
        }

        user = userService.getById(p0.author.id)
        var gear = user.gearset
        // Create a new gearset
        if (gear == null) {
            gear = GearsetEntity(0, user)
        }

        // Parse all of the fields
        val input = p0.args
        try {
            val ap = GearArgumentTag.getAp(input)
            val dp = GearArgumentTag.getDp(input)
            val aap = GearArgumentTag.getAwkAp(input)
            val level = GearArgumentTag.getLevel(input)
            val axe = GearArgumentTag.getAxe(input)
            val bdoClass = GearArgumentTag.getClass(input)
            val state = GearArgumentTag.getState(input)
            val planner = GearArgumentTag.getPlannerLink(input)

            // Populate them if they were filled out
            if (ap != null) {
                gear.ap = ap
            }
            if (dp != null) {
                gear.dp = dp
            }
            if (aap != null) {
                gear.awkAp = aap
            }
            if (level != null) {
                gear.level = level
            }
            if (axe != null) {
                gear.axe = axe
            }
            if (bdoClass != null) {
                gear.characterClass = bdoClass.display
            }
            if (state != null) {
                gear.classState = state.id
            }
            if (planner != null) {
                gear.plannerLink = planner
            }
            if (p0.message.attachments.isNotEmpty() && p0.message.attachments[0].isImage) {
                gear.gearImgLink = p0.message.attachments[0].url
            }
        } catch (e : InvalidGearFieldException) {
            p0.replyWarning(e.message)
            return
        }

        val familyName = GearArgumentTag.getFamilyName(input)
        if (familyName != null) {
            user.familyName = familyName
        }

        user.gearset = gear
        userService.save(user)
        p0.reactSuccess()
    }

    private fun buildEmbedForGear(user: UserEntity, gear: GearsetEntity) : MessageEmbed {
        val builder = EmbedBuilder()
        val name = if (user.familyName == null) user.name else user.familyName
        builder.setTitle("$name's Gear")
        val ap = if (gear.ap != null) "${gear.ap}" else "NA"
        val dp = if (gear.dp != null) "${gear.dp}" else "NA"
        val awk = if (gear.awkAp != null) "${gear.awkAp}" else "NA"
        builder.addField("Gear score", "$ap/$awk/$dp (${gear.getGearScore()})", true)

        if (gear.level != null) {
            builder.addField("Level", "${gear.level}", true)
        }

        if (gear.axe != null) {
            builder.addField("Trina axe", FormattingUtils.getEnhancementString(gear.axe!!), true)
        }

        if (gear.characterClass != null) {
            builder.addField("Class", gear.getClassName(), false)
        }

        if (gear.plannerLink != null) {
            builder.setDescription("[Bdo Planner](${gear.plannerLink})")
        }

        if (gear.gearImgLink != null) {
            builder.setImage(gear.gearImgLink)
        }

        return builder.build()
    }
}