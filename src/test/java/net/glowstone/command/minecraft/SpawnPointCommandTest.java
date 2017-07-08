package net.glowstone.command.minecraft;

import com.google.common.collect.ImmutableList;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
@PrepareForTest({Bukkit.class, CommandUtils.class, GlowServer.class, GlowWorld.class})
public class SpawnPointCommandTest {

    private CommandSender sender, opSender, opPlayer;

    private Player fakePlayer1, fakePlayer2, fakePlayer3;

    private Command command;

    private GlowWorld world;

    private GlowServer server;

    @Before
    public void before() {
        server = PowerMockito.mock(GlowServer.class);

        fakePlayer1 = PowerMockito.mock(Player.class);
        fakePlayer2 = PowerMockito.mock(Player.class);
        fakePlayer3 = PowerMockito.mock(Player.class);

        sender = PowerMockito.mock(CommandSender.class);
        opSender = PowerMockito.mock(CommandSender.class);
        opPlayer = PowerMockito.mock(Player.class);
        world = PowerMockito.mock(GlowWorld.class);
        command = new SpawnPointCommand();

        Mockito.when(fakePlayer1.getName()).thenReturn("player1");
        Mockito.when(fakePlayer2.getName()).thenReturn("player2");
        Mockito.when(fakePlayer3.getName()).thenReturn("thePlayer3");
        Mockito.when(fakePlayer1.getLocation()).thenReturn(new Location(world, 10.5, 20.5, 30.5));
        Mockito.when(fakePlayer2.getLocation()).thenReturn(new Location(world, 10.5, 20.5, 30.5));
        Mockito.when(fakePlayer3.getLocation()).thenReturn(new Location(world, 10.5, 20.5, 30.5));
        Mockito.when(fakePlayer1.getType()).thenReturn(EntityType.PLAYER);
        Mockito.when(fakePlayer2.getType()).thenReturn(EntityType.PLAYER);
        Mockito.when(fakePlayer3.getType()).thenReturn(EntityType.PLAYER);

        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opSender.getServer()).thenReturn(server);

        Mockito.when(opPlayer.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opPlayer.getName()).thenReturn("ChuckNorris");
        Mockito.when(((Entity)opPlayer).getLocation()).thenReturn(new Location(world, 10.5, 20.5, 30.5));

        Mockito.doReturn(ImmutableList.of(fakePlayer1, fakePlayer2, fakePlayer3))
                .when(server).getOnlinePlayers();

        Mockito.when(world.getMaxHeight()).thenReturn(50);
        Mockito.when(world.getEntities()).thenReturn(ImmutableList.of(fakePlayer1, fakePlayer2, fakePlayer3));

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getPlayerExact("player1")).thenReturn(fakePlayer1);
        Mockito.when(Bukkit.getPlayerExact("player2")).thenReturn(fakePlayer2);
        Mockito.when(Bukkit.getPlayerExact("thePlayer3")).thenReturn(fakePlayer3);

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
    public void testExecuteFailsWithTwoParameters() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[2]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Usage: /spawnpoint OR /spawnpoint <player> OR /spawnpoint <player> <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithThreeParameters() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[3]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Usage: /spawnpoint OR /spawnpoint <player> OR /spawnpoint <player> <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithSenderNotPlayer() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Current sender is not a player."));
    }

    @Test
    public void testExecuteFailsUnknownTarget() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"player"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Player 'player' cannot be found"));
    }

    @Test
    public void testExecuteFailsWithDefaultLocation() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"player1"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Default coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithRelativeLocation() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"player1", "~2", "3", "4"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "Relative coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooHigh() {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final boolean commandResult = command.execute(opSender, "label", new String[]{"player1", "2", "10000", "4"});

        assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(captor.capture());
        assertThat(captor.getValue(), is(ChatColor.RED + "'10000.5' is too high for the current world. Max value is '50'."));
    }

    @Test
    public void testExecuteSucceedsWithCurrentLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[0]);

        assertThat(commandResult, is(true));
        Mockito.verify((Player) opPlayer).setBedSpawnLocation(new Location(world, 10.5, 20.5, 30.5), true);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithCurrentLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"player1"});

        assertThat(commandResult, is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1")).setBedSpawnLocation(new Location(world, 10.5, 20.5, 30.5), true);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithSpecificLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"player1", "30", "20", "10"});

        assertThat(commandResult, is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1")).setBedSpawnLocation(new Location(world, 30.5, 20.5, 10.5), true);
    }

    @Test
    public void testExecuteSucceedsAllPlayersWithCurrentLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"@a"});

        assertThat(commandResult, is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1")).setBedSpawnLocation(new Location(world, 10.5, 20.5, 30.5), true);
        Mockito.verify(Bukkit.getPlayerExact("player2")).setBedSpawnLocation(new Location(world, 10.5, 20.5, 30.5), true);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3")).setBedSpawnLocation(new Location(world, 10.5, 20.5, 30.5), true);
    }

    @Test
    public void testExecuteSucceedsAllPlayersWithSpecificLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"@a", "30", "20", "10"});

        assertThat(commandResult, is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1")).setBedSpawnLocation(new Location(world, 30.5, 20.5, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("player2")).setBedSpawnLocation(new Location(world, 30.5, 20.5, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3")).setBedSpawnLocation(new Location(world, 30.5, 20.5, 10.5), true);
    }

    @Test
    public void testExecuteSucceedsAllPlayersWithRelativeLocation() {
        final boolean commandResult = command.execute(opPlayer, "label", new String[]{"@a", "30", "~20", "10"});

        assertThat(commandResult, is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1")).setBedSpawnLocation(new Location(world, 30.5, 41, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("player2")).setBedSpawnLocation(new Location(world, 30.5, 41, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3")).setBedSpawnLocation(new Location(world, 30.5, 41, 10.5), true);
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(opSender, "alias", new String[0]), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{""}), is(ImmutableList.of("player1", "player2", "thePlayer3")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"player"}), is(ImmutableList.of("player1", "player2")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"th"}), is(ImmutableList.of("thePlayer3")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"12", "test"}), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"player", "test"}), is(Collections.emptyList()));
    }
}
