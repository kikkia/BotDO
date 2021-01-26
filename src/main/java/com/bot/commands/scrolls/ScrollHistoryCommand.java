package com.bot.commands.scrolls;

import com.bot.models.ScrollHistory;
import com.bot.service.ScrollHistoryService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ScrollHistoryCommand extends Command {
    ScrollHistoryService scrollHistoryService;
    private Paginator.Builder builder;

    public ScrollHistoryCommand(ScrollHistoryService scrollHistoryService, EventWaiter waiter) {
        this.name = "scrollhistory";
        this.aliases = new String[] {"sh", "shistory"};
        this.help = "Shows all of the scrolls you have completed.";
        this.arguments = "";
        this.scrollHistoryService = scrollHistoryService;

        this.builder = new Paginator.Builder()
                .setColumns(1)
                .setItemsPerPage(1)
                .useNumberedItems(false)
                .showPageNumbers(true)
                .setEventWaiter(waiter)
                .setTimeout(30, TimeUnit.SECONDS)
                .waitOnSinglePage(false)
                .setFinalAction(message -> message.clearReactions().queue());
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        List<ScrollHistory> historyList = scrollHistoryService.getByUser(commandEvent.getAuthor());
        if (historyList.isEmpty()) {
            commandEvent.replyWarning("I have no history of you completing any scrolls.");
            return;
        }

        builder.setText("Scroll history for " + historyList.get(0).getUser().getFamilyName())
                .setUsers(commandEvent.getAuthor())
                .setColor(commandEvent.getSelfMember().getColor());

        for (ScrollHistory h : historyList) {
            builder.addItems(h.toMessage());
        }

        builder.build().paginate(commandEvent.getChannel(), 1);
    }
}
