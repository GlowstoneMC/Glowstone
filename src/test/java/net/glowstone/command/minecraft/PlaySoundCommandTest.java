package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
public class PlaySoundCommandTest extends CommandTestWithFakePlayers {

    protected CommandSender opPlayer;

    public PlaySoundCommandTest() {
        super("player1", "player2", "thePlayer3");
    }

    @Before
    public void before() {
        super.before();
        opPlayer = PowerMockito.mock(Player.class);
        command = new PlaySoundCommand();
        Mockito.when(sender.getServer()).thenReturn(server);
        Mockito.when(opSender.getServer()).thenReturn(server);
        Mockito.when(opPlayer.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(opPlayer.getName()).thenReturn("ChuckNorris");
        Mockito.when(((Entity) opPlayer).getLocation()).thenReturn(location);
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
    }

    @Test
    public void testExecuteFailsWithIncorrectSound() {
        assertThat(command.execute(opSender, "label", new String[]{"noise", "source", "player"}),
            is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "'noise' is not a valid sound."));
    }

    @Test
    public void testExecuteFailsWithIncorrectSoundCategory() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "source", "player"}), is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "'source' is not a valid sound category."));
    }

    @Test
    public void testExecuteFailsUnknownTarget() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "Player 'player' cannot be found"));
    }

    @Test
    public void testExecuteFailsMinimumVolumeOutOfRange() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "300", "2", "300"}), is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "Minimum volume value (300) must be between 0 and 1"));
    }

    @Test
    public void testExecuteFailsMinimumVolumeInvalid() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "300", "2", "volume"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "'volume' is not a valid number"));
    }

    @Test
    public void testExecuteFailsPitchOutOfRange() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "300", "300", "1"}), is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "Pitch value (300) must be between 0 and 2"));
    }

    @Test
    public void testExecuteFailsPitchInvalid() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "300", "pitch", "1"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "'pitch' is not a valid number"));
    }

    @Test
    public void testExecuteFailsVolumeInvalid() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "volume", "1", "1"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "'volume' is not a valid number"));
    }

    @Test
    public void testExecuteFailsPositionInvalid() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "Y", "30", "1", "1"}), is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED + "The position (0,0,Y) is invalid"));
    }

    @Test
    public void testExecuteFailsPlayerTooFarAway() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "100000",
                "100000", "100000"}), is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "player1 is too far away to hear the sound"));
    }

    @Test
    public void testExecuteSucceedsWithoutMinecraftPrefix() {
        assertThat(command.execute(opSender, "label",
            new String[]{"entity.parrot.imitate.wither", "master", "player1"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 10.5, 20.0, 30.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 1, 1);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithCurrentLocation() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 10.5, 20.0, 30.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 1, 1);
    }

    @Test
    public void testExecuteSucceedsOnAllPlayersWithCurrentLocation() {
        assertThat(command.execute(opPlayer, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "@a"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 10.5, 20.0, 30.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 1, 1);
        Mockito.verify(Bukkit.getPlayerExact("player2"))
            .playSound(new Location(world, 10.5, 20.0, 30.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 1, 1);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3"))
            .playSound(new Location(world, 10.5, 20.0, 30.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 1, 1);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithSpecificLocation() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "200"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 0.5, 0.0, 0.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, 1);
    }

    @Test
    public void testExecuteSucceedsOnAllPlayersWithSpecificLocation() {
        assertThat(command.execute(opPlayer, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "@a", "0", "0", "0",
                "200"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 0.5, 0.0, 0.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, 1);
        Mockito.verify(Bukkit.getPlayerExact("player2"))
            .playSound(new Location(world, 0.5, 0.0, 0.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, 1);
        Mockito.verify(Bukkit.getPlayerExact("thePlayer3"))
            .playSound(new Location(world, 0.5, 0.0, 0.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, 1);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithRelativeLocation() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "~20",
                "~20", "~5", "200"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 31, 40.0, 36), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, 1);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithSmallPitch() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "200", "0.05"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 0.5, 0.0, 0.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, (float) 0.5);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithSpecificPitch() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "200", "0.6"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(new Location(world, 0.5, 0.0, 0.5), Sound.ENTITY_PARROT_IMITATE_WITHER,
                SoundCategory.MASTER, 200, (float) 0.6);
    }

    @Test
    public void testExecuteSucceedsOnAnotherPlayerWithMinimumVolume() {
        assertThat(command.execute(opSender, "label",
            new String[]{"minecraft:entity.parrot.imitate.wither", "master", "player1", "0", "0",
                "0", "10", "0.6", "1"}), is(true));
        Mockito.verify(Bukkit.getPlayerExact("player1"))
            .playSound(any(Location.class), eq(Sound.ENTITY_PARROT_IMITATE_WITHER),
                eq(SoundCategory.MASTER), eq((float) 1), eq((float) 0.6));
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(null, null, null), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[0]),
            is(Collections.emptyList()));

        assertThat(command
                .tabComplete(opSender, "alias", new String[]{"minecraft:entity.parrot.imitate.wither"}),
            is(ImmutableList.of("minecraft:entity.parrot.imitate.wither",
                "minecraft:entity.parrot.imitate.wither.skeleton")));
        assertThat(
            command.tabComplete(opSender, "alias", new String[]{"entity.parrot.imitate.wither"}),
            is(ImmutableList.of("minecraft:entity.parrot.imitate.wither",
                "minecraft:entity.parrot.imitate.wither.skeleton")));
        assertThat(command.tabComplete(opSender, "alias",
            new String[]{"chuckNorris:entity.parrot.imitate.wither"}),
            is(ImmutableList.of("minecraft:entity.parrot.imitate.wither",
                "minecraft:entity.parrot.imitate.wither.skeleton")));

        assertThat(command.tabComplete(opSender, "alias", new String[]{"sound", "hosti"}),
            is(ImmutableList.of("hostile")));

        assertThat(command.tabComplete(opSender, "alias", new String[]{"sound", "hostile", ""}),
            is(ImmutableList.of("player1", "player2", "thePlayer3")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"sound", "hostile", "play"}),
            is(ImmutableList.of("player1", "player2")));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"sound", "hostile", "the"}),
            is(ImmutableList.of("thePlayer3")));

        assertThat(command.tabComplete(opSender, "alias", new String[4]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[5]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[6]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[7]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[8]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[9]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[10]),
            is(Collections.emptyList()));
    }
}
