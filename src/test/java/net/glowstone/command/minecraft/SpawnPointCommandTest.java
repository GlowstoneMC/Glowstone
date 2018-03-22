package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, CommandUtils.class})
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

        final Location location = new Location(world, 10.5, 20.0, 30.5);
        Mockito.when(fakePlayer1.getName()).thenReturn("player1");
        Mockito.when(fakePlayer2.getName()).thenReturn("player2");
        Mockito.when(fakePlayer3.getName()).thenReturn("thePlayer3");
        Mockito.when(fakePlayer1.getLocation()).thenReturn(location);
        Mockito.when(fakePlayer2.getLocation()).thenReturn(location);
        Mockito.when(fakePlayer3.getLocation()).thenReturn(location);
        Mockito.when(fakePlayer1.getType()).thenReturn(EntityType.PLAYER);
        Mockito.when(fakePlayer2.getType()).thenReturn(EntityType.PLAYER);
        Mockito.when(fakePlayer3.getType()).thenReturn(EntityType.PLAYER);

        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opSender.getServer()).thenReturn(server);

        Mockito.when(opPlayer.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opPlayer.getName()).thenReturn("ChuckNorris");
        Mockito.when(((Entity) opPlayer).getLocation()).thenReturn(location);

        Mockito.doReturn(ImmutableList.of(fakePlayer1, fakePlayer2, fakePlayer3))
            .when(server).getOnlinePlayers();

        Mockito.when(world.getMaxHeight()).thenReturn(50);
        Mockito.when(world.getEntities())
            .thenReturn(ImmutableList.of(fakePlayer1, fakePlayer2, fakePlayer3));

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getPlayerExact("player1")).thenReturn(fakePlayer1);
        Mockito.when(Bukkit.getPlayerExact("player2")).thenReturn(fakePlayer2);
        Mockito.when(Bukkit.getPlayerExact("thePlayer3")).thenReturn(fakePlayer3);

        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(false));
        Mockito.verify(sender).sendMessage(eq(ChatColor.RED
            + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error."));
    }

    @Test
    public void testExecuteFailsWithTwoParameters() {
        assertThat(command.execute(opSender, "label", new String[2]), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "Usage: /spawnpoint OR /spawnpoint <player> OR /spawnpoint <player> <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithThreeParameters() {
        assertThat(command.execute(opSender, "label", new String[3]), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "Usage: /spawnpoint OR /spawnpoint <player> OR /spawnpoint <player> <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithSenderNotPlayer() {
        assertThat(command.execute(opSender, "label", new String[0]), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "You must specify which player you wish to perform this action on."));
    }

    @Test
    public void testExecuteFailsUnknownTarget() {
        assertThat(command.execute(opSender, "label", new String[]{"player"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "Player 'player' cannot be found"));
    }

    @Test
    public void testExecuteFailsWithDefaultLocation() {
        assertThat(command.execute(opSender, "label", new String[]{"player1"}), is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "Default coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithRelativeLocation() {
        assertThat(command.execute(opSender, "label", new String[]{"player1", "~2", "3", "4"}),
            is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "Relative coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooHigh() {
        assertThat(command.execute(opSender, "label", new String[]{"player1", "2", "10000", "4"}),
            is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "'10000.0' is too high for the current world. Max value is '50'."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooSmall() {
        assertThat(command.execute(opSender, "label", new String[]{"player1", "2", "-10000", "4"}),
            is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "The y coordinate (-10000.0) is too small, it must be at least 0."));
    }

    @Test
    public void testExecuteSucceedsWithCurrentLocation() {
        assertThat(command.execute(opPlayer, "label", new String[0]), is(true));
        Mockito.verify((Player) opPlayer)
            .setBedSpawnLocation(new Location(world, 10.5, 20.0, 30.5), true);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithCurrentLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"player1"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .setBedSpawnLocation(new Location(world, 10.5, 20.0, 30.5), true);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithSpecificLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"player1", "30", "20", "10"}),
            is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .setBedSpawnLocation(new Location(world, 30.5, 20.0, 10.5), true);
    }

    @Test
    public void testExecuteSucceedsAllPlayersWithCurrentLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"@a"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .setBedSpawnLocation(new Location(world, 10.5, 20.0, 30.5), true);
        Mockito.verify(Bukkit.getPlayerExact("player2"))
            .setBedSpawnLocation(new Location(world, 10.5, 20.0, 30.5), true);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3"))
            .setBedSpawnLocation(new Location(world, 10.5, 20.0, 30.5), true);
    }

    @Test
    public void testExecuteSucceedsAllPlayersWithSpecificLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"@a", "30", "20", "10"}),
            is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .setBedSpawnLocation(new Location(world, 30.5, 20.0, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("player2"))
            .setBedSpawnLocation(new Location(world, 30.5, 20.0, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3"))
            .setBedSpawnLocation(new Location(world, 30.5, 20.0, 10.5), true);
    }

    @Test
    public void testExecuteSucceedsAllPlayersWithRelativeLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"@a", "30", "~20", "10"}),
            is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .setBedSpawnLocation(new Location(world, 30.5, 40.0, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("player2"))
            .setBedSpawnLocation(new Location(world, 30.5, 40.0, 10.5), true);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3"))
            .setBedSpawnLocation(new Location(world, 30.5, 40.0, 10.5), true);
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(opSender, "alias", new String[0]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{""}),
            is(ImmutableList.of("player1", "player2", "thePlayer3")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"player"}),
            is(ImmutableList.of("player1", "player2")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"th"}),
            is(ImmutableList.of("thePlayer3")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"12", "test"}),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"player", "test"}),
            is(Collections.emptyList()));
    }
}
