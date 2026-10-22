package com.bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;

import java.util.Locale;

public class RetiredCommand extends SlashCommand {

    public static final String MESSAGE = "This bot now only supports guild-history lookups. "
            + "Use `/whois` to look up a family and `/defaultregion` to set your region. "
            + "If you'd like this command restored, DM kikkia.";

    public RetiredCommand(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.help = "This command is retired; the bot now only supports guild-history lookups.";
        this.guildOnly = false;
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply(MESSAGE).setEphemeral(true).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(MESSAGE);
    }
}
