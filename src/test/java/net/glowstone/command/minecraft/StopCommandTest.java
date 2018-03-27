package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StopCommandTest {
    private GlowServer server;

    private CommandSender sender, opSender;

    private Command command;

    @BeforeEach
    public void before() {
        command = new StopCommand();
        sender = Mockito.mock(CommandSender.class);
        opSender = Mockito.mock(CommandSender.class);
        server = Mockito.mock(GlowServer.class);
        ServerProvider.setMockServer(server);
        when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
        when(opSender.getServer()).thenReturn(server);
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(false));
        Mockito.verify(sender).sendMessage(eq(ChatColor.RED
                + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @Test
    public void testNoArgs() {
        assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(server).shutdown();
    }

    @Test
    public void testWithArgs() {
        assertThat(command.execute(opSender, "label",
                new String[]{"This", "is", "a", "custom", "shutdown", "message"}), is(true));
        Mockito.verify(server).shutdown("This is a custom shutdown message");
    }
}
