package com.bot.commands.scrolls;

import com.bot.models.Scroll;
import com.bot.service.ScrollInventoryService;
import com.bot.utils.CommandParsingUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kotlin.Pair;
import org.springframework.stereotype.Component;

@Component
public class AddScrollsCommand extends Command {

    ScrollInventoryService inventoryService;

    public AddScrollsCommand(ScrollInventoryService inventoryService) {
        this.name = "addscrolls";
        this.aliases = new String[] {"as", "addscroll"};
        this.help = "Add scrolls to your inventory";
        this.arguments = "<Scroll name and count to add, comma separate " +
                "multiple scroll types>";
        this.inventoryService = inventoryService;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify what scrolls to add.");
            return;
        }

        try {
            var scrollPairs = CommandParsingUtils.parseScrollUpdates(commandEvent.getArgs());
            var scrollInventory = inventoryService.getByUser(commandEvent.getAuthor().getId());
            for (Pair<Scroll, Integer> update : scrollPairs) {
                scrollInventory.addScroll(update.getFirst(), update.getSecond());
            }
            inventoryService.save(scrollInventory);
            commandEvent.replySuccess("Updated your inventory!");
        } catch (IllegalArgumentException e) {
            commandEvent.replyWarning(e.getMessage() + " Please ensure you are using the format of scroll_name #` " +
                    "and separate multiple scrolls with commas\"");
        }
    }
}
