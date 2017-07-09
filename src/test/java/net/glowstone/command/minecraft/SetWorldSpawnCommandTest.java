package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
@PrepareForTest({CommandUtils.class, GlowServer.class, GlowWorld.class})
public class SetWorldSpawnCommandTest {

    private CommandSender sender, opSender, opPlayer;

    private GlowWorld world;

    private Command command;

    @Before
    public void before() {
        sender = PowerMockito.mock(CommandSender.class);
        opSender = PowerMockito.mock(CommandSender.class);
        opPlayer = PowerMockito.mock(Player.class);
        world = PowerMockito.mock(GlowWorld.class);
        command = new SetWorldSpawnCommand();

        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);

        Mockito.when(opPlayer.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opPlayer.getName()).thenReturn("ChuckNorris");
        Mockito.when(((Entity)opPlayer).getLocation()).thenReturn(new Location(world, 10.5, 20.5, 30.5));

        Mockito.when(world.getMaxHeight()).thenReturn(50);

        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class)).toReturn(world);
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
    public void testExecuteFailsWithoutWorld() {
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class)).toReturn(null);
        final boolean commandResult = command.execute(sender, "label", new String[0]);

        assertThat(commandResult, is(false));
    }

    @Test
    public void testExecuteFailsWithOneParameter() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[1]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Usage: /setworldspawn OR /setworldspawn <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithTwoParameters() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[2]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Usage: /setworldspawn OR /setworldspawn <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithDefaultLocation() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Default coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithRelativeLocation() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"~2", "3", "4"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Relative coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooHigh() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"2", "10000", "4"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "'10000' is too high for the current world. Max value is '50'."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooSmall() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"2", "-10000", "4"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        // -10001 because of the floor, it's not supposed to be negative
        assertThat(captor.getValue(), is(ChatColor.RED + "The y coordinate (-10001) is too small, it must be at least 0."));
    }

    @Test
    public void testExecuteSucceedsWithCurrentLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[0]);

        assertThat(commandResult, is(true));
        Mockito.verify(world).setSpawnLocation(10, 20, 30);
    }

    @Test
    public void testExecuteSucceedsWithSpecificLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"30", "20", "10"});

        assertThat(commandResult, is(true));
        Mockito.verify(world).setSpawnLocation(30, 20, 10);
    }

    @Test
    public void testExecuteSucceedsWithRelativeLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"30", "~20", "10"});

        assertThat(commandResult, is(true));
        Mockito.verify(world).setSpawnLocation(30, 41, 10);
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(opSender, "alias", new String[0]), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test"}), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test", "test"}), is(Collections.emptyList()));
    }
}
