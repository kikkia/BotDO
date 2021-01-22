package com.bot.commands.scrolls;

import com.bot.service.ScrollInventoryService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ScrollsCommand extends Command {

    private ScrollInventoryService inventoryService;

    public ScrollsCommand(ScrollInventoryService service) {
        this.name = "scrolls";
        this.help = "Fetch all current summon scrolls for you are a given user.";
        this.arguments = "<none, or user's id/mention>";
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;

        this.inventoryService = service;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        var userIds = new ArrayList<String>();

        if (!commandEvent.getMessage().getMentionedUsers().isEmpty()) {
            // Parse mentioned users
            userIds.addAll(commandEvent.getMessage().getMentionedUsers()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList()));
        } else if (!commandEvent.getArgs().isEmpty()) {
            // Parse user ids or TODO: Family names
            var mentioned = commandEvent.getArgs().split(" ");
            for (var s : mentioned) {
                try {
                    Long.parseLong(s);
                } catch (Exception e) {
                    // not a userid
                    continue;
                }
                userIds.add(s);
            }
        } else {
            // If no mentioned, get the inventory of the user
            userIds.add(commandEvent.getAuthor().getId());
        }

        if (userIds.size() > 5) {
            commandEvent.replyWarning("Cannot display scroll inventories for more than 5 users at a time.");
        }

        var inventories = inventoryService.getByUserIds(userIds);
        for (var inventory : inventories) {
            commandEvent.reply(inventory.toMessage());
        }
    }
}
