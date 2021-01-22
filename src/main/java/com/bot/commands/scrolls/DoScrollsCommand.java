package com.bot.commands.scrolls;

import com.bot.service.ScrollHistoryService;
import com.bot.service.ScrollInventoryService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.stereotype.Component;

@Component
public class DoScrollsCommand extends Command {

    private ScrollHistoryService historyService;
    private ScrollInventoryService inventoryService;

    public DoScrollsCommand(ScrollHistoryService historyService, ScrollInventoryService inventoryService) {
        this.name = "doscrolls";
        this.aliases = new String[] {"doscroll"};
        this.help = "Marks all or given scrolls as done and logs them in your scroll history";
        this.arguments = "<nothing for all, scroll_name # for specific, comma separated>";
        this.historyService = historyService;
        this.inventoryService = inventoryService;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

    }
}
