package com.bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestRetiredCommand {

    @Test
    void retiredCommandOnlyRepliesWithAPrivateNotice() {
        SlashCommandEvent event = mock(SlashCommandEvent.class);
        ReplyCallbackAction reply = mock(ReplyCallbackAction.class);
        when(event.reply(RetiredCommand.MESSAGE)).thenReturn(reply);
        when(reply.setEphemeral(true)).thenReturn(reply);

        RetiredCommand command = new RetiredCommand("OldCommand");
        command.execute(event);

        assertEquals("oldcommand", command.getName());
        assertEquals(0, command.getUserPermissions().length);
        assertEquals(0, command.getBotPermissions().length);
        assertTrue(RetiredCommand.MESSAGE.contains("DM kikkia"));
        verify(reply).queue();
        verify(event).reply(RetiredCommand.MESSAGE);
        verifyNoMoreInteractions(event);
    }

    @Test
    void retiredPrefixCommandOnlyRepliesWithTheSameNotice() {
        CommandEvent event = mock(CommandEvent.class);

        new RetiredCommand("gear").execute(event);

        verify(event).reply(RetiredCommand.MESSAGE);
        verifyNoMoreInteractions(event);
    }
}
