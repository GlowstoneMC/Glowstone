package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

import java.util.Collections;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandTest;
import net.glowstone.command.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommandUtils.class})
public class SetWorldSpawnCommandTest extends CommandTest<SetWorldSpawnCommand> {

    protected CommandSender opPlayer;

    private GlowWorld world;

    public SetWorldSpawnCommandTest() {
        super(SetWorldSpawnCommand::new);
    }

    @Before
    @Override
    public void before() {
        super.before();
        world = PowerMockito.mock(GlowWorld.class);
        opPlayer = prepareMockPlayers(
                new Location(world, 10.5, 20.0, 30.5), null, world, "ChuckNorris")[0];
        Mockito.when(opPlayer.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(world.getMaxHeight()).thenReturn(50);
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
    }

    @Test
    public void testExecuteFailsWithoutWorld() {
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(null);

        assertThat(command.execute(opSender, "label", new String[0]), is(false));
    }

    @Test
    public void testExecuteFailsWithOneParameter() {
        assertThat(command.execute(opSender, "label", new String[1]), is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "Usage: /setworldspawn OR /setworldspawn <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithTwoParameters() {
        assertThat(command.execute(opSender, "label", new String[2]), is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "Usage: /setworldspawn OR /setworldspawn <x> <y> <z>"));
    }

    @Test
    public void testExecuteFailsWithDefaultLocation() {
        assertThat(command.execute(opSender, "label", new String[0]), is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "Default coordinates can not be used without a physical user."));
        ;
    }

    @Test
    public void testExecuteFailsWithRelativeLocation() {
        assertThat(command.execute(opSender, "label", new String[]{"~2", "3", "4"}), is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "Relative coordinates can not be used without a physical user."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooHigh() {
        assertThat(command.execute(opSender, "label", new String[]{"2", "10000", "4"}), is(false));
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "'10000' is too high for the current world. Max value is '50'."));
    }

    @Test
    public void testExecuteFailsWithYCoordinatesTooSmall() {
        assertThat(command.execute(opSender, "label", new String[]{"2", "-10000", "4"}), is(false));
        // -10001 because of the floor, it's not supposed to be negative
        Mockito.verify(opSender).sendMessage(
            eq(ChatColor.RED + "The y coordinate (-10000) is too small, it must be at least 0."));
    }

    @Test
    public void testExecuteSucceedsWithCurrentLocation() {
        assertThat(command.execute(opPlayer, "label", new String[0]), is(true));
        Mockito.verify(world).setSpawnLocation(10, 20, 30);
    }

    @Test
    public void testExecuteSucceedsWithSpecificLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"30", "20", "10"}), is(true));
        Mockito.verify(world).setSpawnLocation(30, 20, 10);
    }

    @Test
    public void testExecuteSucceedsWithRelativeLocation() {
        assertThat(command.execute(opPlayer, "label", new String[]{"30", "~20", "10"}), is(true));
        Mockito.verify(world).setSpawnLocation(30, 40, 10);
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(opSender, "alias", new String[0]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test"}),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test", "test"}),
            is(Collections.emptyList()));
    }
}
