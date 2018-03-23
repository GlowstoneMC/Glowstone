package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class SetIdleTimeoutCommandTest {

    private CommandSender sender, opSender;

    private Command command;

    @Before
    public void before() {
        PowerMockito.mockStatic(Bukkit.class);

        sender = PowerMockito.mock(CommandSender.class);
        opSender = PowerMockito.mock(CommandSender.class);
        command = new SetIdleTimeoutCommand();

        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(GlowServerProvider.getServer()).thenReturn(PowerMockito.mock(Server.class));
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        final boolean commandResult = command.execute(sender, "label", new String[0]);

        assertThat(commandResult, is(false));
        Mockito.verify(sender).sendMessage(eq(ChatColor.RED
            + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @Test
    public void testExecuteFailsWithoutParameters() {
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "Usage: /setidletimeout <Minutes until kick>"));
    }

    @Test
    public void testExecuteFailsWithIncorrectNumber() {
        final boolean commandResult = command
            .execute(opSender, "label", new String[]{"invalidNumber"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "'invalidNumber' is not a valid number"));
    }

    @Test
    public void testExecuteFailsWithNegativeTimeout() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"-42"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "The number you have entered (-42) is too small, it must be at least 1"));
    }

    @Test
    public void testExecuteFailsWithNullTimeout() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"0"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "The number you have entered (0) is too small, it must be at least 1"));
    }

    @Test
    public void testExecuteSucceeds() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"50"});

        assertThat(commandResult, is(true));
        Mockito.verify(opSender)
            .sendMessage(eq("Successfully set the idle timeout to 50 minutes."));
        Mockito.verify(GlowServerProvider.getServer()).setIdleTimeout(50);
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(null, null, null), is(Collections.emptyList()));
        assertThat(command.tabComplete(sender, "", new String[0]), is(Collections.emptyList()));
        assertThat(command.tabComplete(sender, "", new String[]{"12"}),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(sender, "", new String[]{"12", "test"}),
            is(Collections.emptyList()));
    }
}
