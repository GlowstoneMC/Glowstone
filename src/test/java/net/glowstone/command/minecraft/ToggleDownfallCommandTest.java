package net.glowstone.command.minecraft;

import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        PowerMockito.stub(PowerMockito.method(CommandUtils.class, "getWorld", CommandSender.class)).toReturn(world);
    }

    @Test
    public void testExecuteFailsWithoutPermission() {
        final boolean commandResult = command.execute(sender, "label", new String[0]);

        assertThat(commandResult, is(false));
    }

    @Test
    public void testExecuteSetRain() {
        PowerMockito.when(world.hasStorm()).thenReturn(true);
        final ArgumentCaptor<Boolean> stormCaptor = ArgumentCaptor.forClass(Boolean.class);
        final ArgumentCaptor<Boolean> thunderingCaptor = ArgumentCaptor.forClass(Boolean.class);
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        assertThat(commandResult, is(true));
        Mockito.verify(world).setStorm(stormCaptor.capture());
        Mockito.verify(world).setThundering(thunderingCaptor.capture());
        assertThat(stormCaptor.getValue(), is(false));
        assertThat(thunderingCaptor.getValue(), is(false));
    }

    @Test
    public void testExecuteClearWeather() {
        PowerMockito.when(world.hasStorm()).thenReturn(false);
        final ArgumentCaptor<Boolean> stormCaptor = ArgumentCaptor.forClass(Boolean.class);
        final ArgumentCaptor<Boolean> thunderingCaptor = ArgumentCaptor.forClass(Boolean.class);
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        assertThat(commandResult, is(true));
        Mockito.verify(world).setStorm(stormCaptor.capture());
        Mockito.verify(world).setThundering(thunderingCaptor.capture());
        assertThat(stormCaptor.getValue(), is(true));
        assertThat(thunderingCaptor.getValue(), is(true));
    }

    @Test
    public void testTabComplete() {
        assertThat(command.tabComplete(opSender, "alias", new String[0]), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{""}), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test"}), is(Collections.emptyList()));
        assertThat(command.tabComplete(opSender, "alias", new String[]{"test", "test"}), is(Collections.emptyList()));
    }
}
