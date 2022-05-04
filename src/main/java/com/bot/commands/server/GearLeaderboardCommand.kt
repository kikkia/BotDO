package com.bot.commands.server

import com.bot.service.UserService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.jagrosh.jdautilities.menu.Paginator
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class GearLeaderboardCommand(val userService: UserService, val waiter: EventWaiter) : Command() {

    init {
        this.name = "gearboard"
        this.help = "Lists a leaderboard of everyone in the discord channels gear."
        this.guildOnly = true
    }

    override fun execute(command: CommandEvent?) {
        val paginatorBuilder = Paginator.Builder()
                .setColumns(1)
                .setItemsPerPage(15)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .setEventWaiter(waiter)
                .setTimeout(60, TimeUnit.SECONDS)
                .waitOnSinglePage(false)
                .setFinalAction{message -> message.clearReactions().queue()}

        // Get all user entities in channel if they have gear set
        val users = userService.getByIds(
                command!!.textChannel.members.map { it.user.id }.toMutableList())
                .filter { it.gearset != null }.toMutableList()
        // Sort users by total gearscore
        users.sortWith(Comparator{
            x, y -> y.gearset!!.getGearScore().compareTo(x.gearset!!.getGearScore())})

        paginatorBuilder.setText("Gear leaderboard for users in: ${command.channel.name}")
                .addUsers(command.author)
                .setColor(command.selfMember.color)

        for (u in users) {
            paginatorBuilder.addItems(u.toMessageEntry())
        }

        paginatorBuilder.build().paginate(command.channel, 1)
    }
}