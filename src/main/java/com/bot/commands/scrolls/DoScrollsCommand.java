package com.bot.commands.scrolls;

import com.bot.db.mapper.UserMapper;
import com.bot.models.Scroll;
import com.bot.models.ScrollHistory;
import com.bot.service.ScrollHistoryService;
import com.bot.service.ScrollInventoryService;
import com.bot.utils.CommandParsingUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import kotlin.Pair;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DoScrollsCommand extends Command {

    private ScrollHistoryService historyService;
    private ScrollInventoryService inventoryService;

    public DoScrollsCommand(ScrollHistoryService historyService, ScrollInventoryService inventoryService) {
        this.name = "doscrolls";
        this.aliases = new String[] {"doscroll", "ds", "complete"};
        this.help = "Marks all or given scrolls as done and logs them in your scroll history";
        this.arguments = "<nothing for all, scroll_name # for specific, comma separated>";
        this.historyService = historyService;
        this.inventoryService = inventoryService;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            commandEvent.replyWarning("You need to specify what scroll you completed. " +
                    "(You can say `all` to complete all of them)");
            return;
        }
        var all = commandEvent.getArgs().trim().equalsIgnoreCase("all");
        var inventory = inventoryService.getByUser(commandEvent.getAuthor().getId());
        // Get scrolls to remove from inventory, if all, map scrolls in inventory to list, else parse the command
        try {
            var scrollsToRemove = all ? inventory.getScrolls().entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(e -> new Pair<>(e.getKey(), e.getValue()))
                    .collect(Collectors.toList()) :
                    CommandParsingUtils.parseScrollUpdates(commandEvent.getArgs());
            var scrollHistory = new ScrollHistory(inventory.getUser());
            for (Pair<Scroll, Integer> p : scrollsToRemove) {
                inventory.removeScroll(p.getFirst(), p.getSecond());
                scrollHistory.putScroll(p.getFirst(), p.getSecond());
            }
            inventoryService.save(inventory);
            historyService.save(scrollHistory);
        } catch (Exception e) {
            commandEvent.replyWarning(e.getMessage() + " Please ensure you are using the format of `scroll_name #` " +
                    "and separate multiple scrolls with commas");
            return;
        }
        commandEvent.replySuccess("Successfully updated your inventory/history.");
    }
}
