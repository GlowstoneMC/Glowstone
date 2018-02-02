package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

import java.util.Collections;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.World;
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
@PrepareForTest({CommandUtils.class, GlowWorld.class})
public class ToggleDownfallCommandTest {

    private World world;

    private Command command;

    private CommandSender sender, opSender;

    @Before
    public void before() {
        world = PowerMockito.mock(GlowWorld.class);
        command = new ToggleDownfallCommand();
        sender = PowerMockito.mock(CommandSender.class);
        opSender = PowerMockito.mock(CommandSender.class);

        Mockito.when(opSender.hasPermission(Mockito.anyString())).thenReturn(true);
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(false));
    }

    @Test
    public void testExecuteSetRain() {
        PowerMockito.when(world.hasStorm()).thenReturn(true);

        assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(world).setStorm(eq(false));
        Mockito.verify(world).setThundering(eq(false));
    }

    @Test
    public void testExecuteClearWeather() {
        PowerMockito.when(world.hasStorm()).thenReturn(false);

        assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(world).setStorm(eq(true));
        Mockito.verify(world).setThundering(eq(true));
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(opSender, "alias", new String[0]),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{""}),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test"}),
            is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test", "test"}),
            is(Collections.emptyList()));
    }
}
