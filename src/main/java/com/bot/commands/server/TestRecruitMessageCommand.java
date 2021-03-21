package com.bot.commands.server;

import com.bot.db.entities.GuildEntity;
import com.bot.service.GuildService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestRecruitMessageCommand extends Command {

    @Autowired
    private GuildService guildService;

    public TestRecruitMessageCommand() {
        this.name = "testrecruitmessage";
        this.aliases = new String[]{"trm", "testrmessage"};
        this.help = "DMs you the recruit message to test it out";
        this.arguments = "The message to dm new users";
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        GuildEntity guild = guildService.getById(commandEvent.getGuild().getId());

        if (guild.getRecruitMessage() == null) {
            commandEvent.replyWarning("No recruit message set");
            return;
        }

        commandEvent.getAuthor().openPrivateChannel().complete().sendMessage(guild.getRecruitMessage()).queue();
        commandEvent.reactSuccess();
    }
}
