package com.bot.commands.scrolls;

import com.bot.models.Scroll;
import com.bot.service.ScrollInventoryService;
import com.bot.utils.StringUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kotlin.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddScrollsCommand extends Command {

    ScrollInventoryService inventoryService;

    public AddScrollsCommand(ScrollInventoryService inventoryService) {
        this.name = "addscrolls";
        this.aliases = new String[] {"as"};
        this.help = "Add scrolls to your inventory";
        this.arguments = "<Scroll name and count to add, comma separate " +
                "multiple scroll types>";
        this.inventoryService = inventoryService;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.replyWarning("You need to specify what scrolls to add.");
            return;
        }

        var scrolls = commandEvent.getArgs().split(",");

        // Scroll pair denotes a pair of scroll/number
        List<Pair<Scroll, Integer>> scrollPairs = new ArrayList<>();

        try {
            for (String scrollPair : scrolls) {
                var parts = scrollPair.split(" ");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Error found near `" +
                            scrollPair + "` More than 2 parts found.");
                }
                // Find the scroll name and the quantity
                // TODO: Custom exceptions
                Integer quantity;
                Scroll scroll;
                if (StringUtils.isNumeric(parts[0])) {
                    quantity = Integer.parseInt(parts[0]);
                    scroll = Scroll.getScrollForName(parts[1]);
                } else if (StringUtils.isNumeric(parts[1])) {
                    quantity = Integer.parseInt(parts[1]);
                    scroll = Scroll.getScrollForName(parts[0]);
                } else {
                    throw new IllegalArgumentException("Error found near `" +
                            scrollPair + "` No quantity found.");
                }
                if (scroll == null) {
                    throw new IllegalArgumentException("Error found near `" +
                            scrollPair + "` No valid scroll name found.");
                }
                scrollPairs.add(new Pair<>(scroll, quantity));

                var scrollInventory = inventoryService.getByUser(commandEvent.getAuthor().getId());
                for (Pair<Scroll, Integer> update : scrollPairs) {
                    scrollInventory.addScroll(update.getFirst(), update.getSecond());
                }
                inventoryService.save(scrollInventory);
                commandEvent.replySuccess("Updated your inventory!");
            }
        } catch (IllegalArgumentException e) {
            commandEvent.replyWarning(e.getMessage() + " Please ensure you are using the format of scroll_name #` " +
                    "and separate multiple scrolls with commas\"");
            return;
        }
    }
}
