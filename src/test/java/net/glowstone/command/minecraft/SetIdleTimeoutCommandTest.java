package net.glowstone.command.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
        Mockito.when(Bukkit.getServer()).thenReturn(PowerMockito.mock(Server.class));
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(sender, "label", new String[0]);

        assertThat(commandResult, is(false));
        Mockito.verify(sender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @Test
    public void testExecuteFailsWithoutParameters() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Usage: /setidletimeout <Minutes until kick>"));
    }

    @Test
    public void testExecuteFailsWithIncorrectNumber() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"invalidNumber"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "'invalidNumber' is not a valid number"));
    }

    @Test
    public void testExecuteFailsWithNegativeTimeout() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"-42"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Timeout has to be positive"));
    }

    @Test
    public void testExecuteFailsWithNullTimeout() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"0"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Timeout has to be positive"));
    }

    @Test
    public void testExecuteSucceeds() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"50"});
        PowerMockito.verifyStatic();
        Bukkit.broadcastMessage(captor.capture());

        assertThat(commandResult, is(true));
        Mockito.verify(Bukkit.getServer()).setIdleTimeout(50);
        assertThat(captor.getValue(), is("The idle timeout has been set to '50' minutes."));
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(null, null, null), is(Collections.emptyList()));
        assertThat(command.tabComplete(sender, "", new String[0]), is(Collections.emptyList()));
        assertThat(command.tabComplete(sender, "", new String[]{"12"}), is(Collections.emptyList()));
        assertThat(command.tabComplete(sender, "", new String[]{"12", "test"}), is(Collections.emptyList()));
    }
}
