package com.bot.commands.events;

import com.bot.db.entities.EventEntity;
import com.bot.db.entities.EventRoleEntity;
import com.bot.db.entities.GuildEntity;
import com.bot.db.entities.UserEntity;
import com.bot.db.mapper.EventRoleMapper;
import com.bot.service.EventService;
import com.bot.service.GuildService;
import com.bot.service.UserService;
import com.bot.utils.Constants;
import com.bot.utils.FormattingUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class AddEventCommand extends Command {

    @Autowired
    private EventService eventService;

    @Autowired
    private GuildService guildService;

    @Autowired
    private UserService userService;

    private EventWaiter waiter;

    public AddEventCommand(EventWaiter waiter) {
        this.name = "addevent";
        this.help = "Adds a custom event";
        this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};

        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getTextChannel().sendMessage("What's the name of the event?").queue();

        waiter.waitForEvent(MessageReceivedEvent.class,
                e -> e.getAuthor().equals(commandEvent.getAuthor())
                        && e.getChannel().equals(commandEvent.getChannel())
                        && !e.getMessage().equals(commandEvent.getMessage()),
                new StepOneConsumer(commandEvent),
                // if the user takes more than a minute, time out
                1, TimeUnit.MINUTES, () -> commandEvent.reply(Constants.EVENT_WAITER_TIMEOUT));
    }

    // Parses name
    protected class StepOneConsumer implements Consumer<MessageReceivedEvent> {
        private CommandEvent commandEvent;

        StepOneConsumer(CommandEvent commandEvent) {
            this.commandEvent = commandEvent;
        }


        @Override
        public void accept(MessageReceivedEvent messageReceivedEvent) {

            EventEntity eventEntity = new EventEntity();
            eventEntity.setName(messageReceivedEvent.getMessage().getContentStripped());

            commandEvent.reply("What time is the event? Format is 'yyyy-MM-dd HH:MM Timezone' (e.g. CST)");

            waiter.waitForEvent(MessageReceivedEvent.class,
                    e -> e.getAuthor().equals(commandEvent.getAuthor())
                            && e.getChannel().equals(commandEvent.getChannel())
                            && !e.getMessage().equals(commandEvent.getMessage()),
                    new StepTwoConsumer(commandEvent, eventEntity),
                    // if the user takes more than a minute, time out
                    1, TimeUnit.MINUTES, () -> commandEvent.reply(Constants.EVENT_WAITER_TIMEOUT));
        }
    }


    // Parses timestamp
    protected class StepTwoConsumer implements Consumer<MessageReceivedEvent> {
        private CommandEvent commandEvent;
        private EventEntity eventEntity;

        StepTwoConsumer(CommandEvent commandEvent, EventEntity eventEntity) {
            this.commandEvent = commandEvent;
            this.eventEntity = eventEntity;
        }

        @Override
        public void accept(MessageReceivedEvent messageReceivedEvent) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
            Date timeOfEvent;

            try {
                timeOfEvent = format.parse(messageReceivedEvent.getMessage().getContentStripped());
            } catch (Exception e) {
                e.printStackTrace();
                commandEvent.replyWarning("I had a problem parsing that timestamp, please make sure it is the right format.");
                return;
            }

            eventEntity.setNextTime(new Timestamp(timeOfEvent.getTime()));

            commandEvent.reply("What roles should be able to see this event? (Names comma separated or reply 'x' to make" +
                    " this event visible to everyone)");

            waiter.waitForEvent(MessageReceivedEvent.class,
                    e -> e.getAuthor().equals(commandEvent.getAuthor())
                            && e.getChannel().equals(commandEvent.getChannel())
                            && !e.getMessage().equals(commandEvent.getMessage()),
                    new StepThreeConsumer(commandEvent, eventEntity),
                    // if the user takes more than a minute, time out
                    1, TimeUnit.MINUTES, () -> commandEvent.reply(Constants.EVENT_WAITER_TIMEOUT));
        }
    }

    // Parses roles
    protected class StepThreeConsumer implements Consumer<MessageReceivedEvent> {
        private CommandEvent commandEvent;
        private EventEntity eventEntity;

        StepThreeConsumer(CommandEvent commandEvent, EventEntity eventEntity) {
            this.commandEvent = commandEvent;
            this.eventEntity = eventEntity;
        }

        @Override
        public void accept(MessageReceivedEvent messageReceivedEvent) {

            List<EventRoleEntity> roleEntityList = new ArrayList<>();

            if (!messageReceivedEvent.getMessage().getContentDisplay().equals("x")) {
                String[] roleNames = messageReceivedEvent.getMessage().getContentStripped().split(",");
                for (String r : roleNames) {
                    roleEntityList.addAll(messageReceivedEvent.getGuild().getRolesByName(r, true)
                            .stream()
                            .map(role -> EventRoleMapper.Companion.map(eventEntity, role.getId()))
                            .collect(Collectors.toList()));
                }
            }

            eventEntity.setRoles(roleEntityList);
            GuildEntity guildEntity = guildService.getById(commandEvent.getGuild().getId());
            UserEntity authorEntity = userService.getById(commandEvent.getAuthor().getId());
            eventEntity.setGuild(guildEntity);
            eventEntity.setAuthor(authorEntity);
            // Due to some JPA fuckery, we will persist these right after persisting the event
            eventEntity = eventService.addNewCustomEvent(eventEntity);

            commandEvent.replySuccess("Saved event");
        }
    }

}
