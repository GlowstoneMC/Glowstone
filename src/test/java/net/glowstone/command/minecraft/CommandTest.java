package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;

import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
@PrepareForTest({Bukkit.class, CommandUtils.class})
public abstract class CommandTest {
    protected CommandSender sender;
    protected Command command;

    @Test
    public void testExecuteFailsWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(false));
        Mockito.verify(sender).sendMessage(eq(ChatColor.RED
            + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @Before
    public void before() {
        sender = PowerMockito.mock(CommandSender.class);
    }
}
