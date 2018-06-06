package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;

import java.util.Collections;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.hamcrest.MatcherAssert;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@PrepareForTest({CommandUtils.class})
public class ToggleDownfallCommandTest extends CommandTest<ToggleDownfallCommand> {

    private World world;

    public ToggleDownfallCommandTest() {
        super(ToggleDownfallCommand::new);
    }

    @Override
    @BeforeMethod
    public void before() {
        super.before();
        world = PowerMockito.mock(GlowWorld.class);
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class))
            .toReturn(world);
    }

    @Test
    public void testExecuteSetRain() {
        PowerMockito.when(world.hasStorm()).thenReturn(true);

        MatcherAssert.assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(world).setStorm(eq(false));
        Mockito.verify(world).setThundering(eq(false));
    }

    @Test
    public void testExecuteClearWeather() {
        PowerMockito.when(world.hasStorm()).thenReturn(false);

        MatcherAssert.assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(world).setStorm(eq(true));
        Mockito.verify(world).setThundering(eq(true));
    }

    @Test
    public void testTabComplete() {
        MatcherAssert.assertThat(command.tabComplete(opSender, "alias", new String[0]), is(Collections.emptyList()));
        MatcherAssert.assertThat(command.tabComplete(opSender, "alias", new String[]{""}), is(Collections.emptyList()));
        MatcherAssert.assertThat(command.tabComplete(opSender, "alias", new String[]{"test"}), is(Collections.emptyList()));
        MatcherAssert.assertThat(command.tabComplete(opSender, "alias", new String[]{"test", "test"}), is(Collections.emptyList()));
    }
}
