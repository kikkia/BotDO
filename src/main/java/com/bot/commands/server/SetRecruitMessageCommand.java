package com.bot.commands.server;

import com.bot.db.entities.GuildEntity;
import com.bot.service.GuildService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetRecruitMessageCommand extends Command {

    @Autowired
    private GuildService guildService;

    public SetRecruitMessageCommand() {
        this.name = "setrecruitmessage";
        this.help = "Sets the message to dm new recruits";
        this.arguments = "The message to dm new users";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isBlank()) {
            GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
            guildService.setRecruitMessage(guild, null);
            commandEvent.reactSuccess();
            return;
        }

        GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());
        guildService.setRecruitMessage(guild, commandEvent.getArgs());
        commandEvent.reactSuccess();
    }
}
