package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import net.glowstone.command.CommandTestWithFakePlayers;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
public class SpawnPointCommandTest extends CommandTestWithFakePlayers<SpawnPointCommand> {

    private CommandSender opPlayer;

    public SpawnPointCommandTest() {
        super(SpawnPointCommand::new, "player1", "player2", "thePlayer3");
    }

    @Before
    public void before() {
        super.before();

        opPlayer = PowerMockito.mock(Player.class);
        Mockito.when(opSender.getServer()).thenReturn(server);
        Mockito.when(opPlayer.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opPlayer.getName()).thenReturn("ChuckNorris");
        Mockito.when(((Entity) opPlayer).getLocation()).thenReturn(location);
        Mockito.when(world.getMaxHeight()).thenReturn(50);
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
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
            + "Need a target player."));
    }

    @Test
    public void testExecuteFailsUnknownTarget() {
        assertThat(command.execute(opSender, "label", new String[]{"player"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "Player 'player' cannot be found."));
    }

    @Test
    public void testExecuteFailsWithDefaultLocation() {
        assertThat(command.execute(opSender, "label", new String[]{"player1"}), is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "This command needs absolute coordinates when you're not a player or command block."));
    }

    @Test
    public void testExecuteFailsWithRelativeLocation() {
        assertThat(command.execute(opSender, "label", new String[]{"player1", "~2", "3", "4"}),
            is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "This command needs absolute coordinates when you're not a player or command block."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooHigh() {
        assertThat(command.execute(opSender, "label", new String[]{"player1", "2", "10000", "4"}),
            is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "Too high: maximum Y coordinate in this world is 50."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooSmall() {
        assertThat(command.execute(opSender, "label", new String[]{"player1", "2", "-10000", "4"}),
            is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "Too low: Y coordinate can't be negative."));
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
