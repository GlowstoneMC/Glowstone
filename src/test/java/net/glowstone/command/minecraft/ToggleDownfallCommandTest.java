package net.glowstone.command.minecraft;

import net.glowstone.GlowWorld;
import net.glowstone.command.CommandTest;
import net.glowstone.command.CommandUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommandUtils.class})
public class ToggleDownfallCommandTest extends CommandTest<ToggleDownfallCommand> {

    private World world;

    public ToggleDownfallCommandTest() {
        super(ToggleDownfallCommand::new);
    }

    @Override
    @Before
    public void before() {
        super.before();
        world = PowerMockito.mock(GlowWorld.class);
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
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
