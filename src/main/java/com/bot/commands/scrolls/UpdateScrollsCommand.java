package com.bot.commands.scrolls;

import com.bot.models.Scroll;
import com.bot.service.ScrollInventoryService;
import com.bot.utils.CommandParsingUtils;
import com.bot.utils.StringUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kotlin.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateScrollsCommand extends Command {

    ScrollInventoryService inventoryService;

    public UpdateScrollsCommand(ScrollInventoryService inventoryService) {
        this.name = "updatescrolls";
        this.aliases = new String[] {"us"};
        this.help = "Update scroll quantities in your inventory";
        this.arguments = "<Scroll name and count to update, comma separate " +
                "multiple scroll types>";
        this.inventoryService = inventoryService;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.replyWarning("You need to specify what scrolls to add.");
            return;
        }

        try {
            var scrollPairs = CommandParsingUtils.INSTANCE.parseScrollUpdates(commandEvent.getArgs());
            var scrollInventory = inventoryService.getByUser(commandEvent.getAuthor().getId());
            for (Pair<Scroll, Integer> update : scrollPairs) {
                scrollInventory.putScroll(update.getFirst(), update.getSecond());
            }
            inventoryService.save(scrollInventory);
            commandEvent.replySuccess("Updated your inventory!");
        } catch (IllegalArgumentException e) {
            commandEvent.replyWarning(e.getMessage() + " Please ensure you are using the format of scroll_name #` " +
                    "and separate multiple scrolls with commas\"");
        }
    }
}
