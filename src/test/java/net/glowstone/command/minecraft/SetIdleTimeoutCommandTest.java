package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;

import java.util.Collections;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import org.bukkit.ChatColor;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class SetIdleTimeoutCommandTest extends CommandTest {

    @Before
    @Override
    public void before() {
        super.before();
        command = new SetIdleTimeoutCommand();

        ServerProvider.setMockServer(PowerMockito.mock(GlowServer.class));
    }

    @Test
    public void testExecuteFailsWithoutParameters() {
        final boolean commandResult = command.execute(opSender, "label", new String[0]);

        MatcherAssert.assertThat(commandResult, is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "Usage: /setidletimeout <Minutes until kick>"));
    }

    @Test
    public void testExecuteFailsWithIncorrectNumber() {
        final boolean commandResult = command
            .execute(opSender, "label", new String[]{"invalidNumber"});

        MatcherAssert.assertThat(commandResult, is(false));
        Mockito.verify(opSender)
            .sendMessage(eq(ChatColor.RED + "'invalidNumber' is not a valid number"));
    }

    @Test
    public void testExecuteFailsWithNegativeTimeout() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"-42"});

        MatcherAssert.assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "The number you have entered (-42) is too small, it must be at least 1"));
    }

    @Test
    public void testExecuteFailsWithNullTimeout() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"0"});

        MatcherAssert.assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "The number you have entered (0) is too small, it must be at least 1"));
    }

    @Test
    public void testExecuteSucceeds() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"50"});

        MatcherAssert.assertThat(commandResult, is(true));
        Mockito.verify(opSender)
            .sendMessage(eq("Successfully set the idle timeout to 50 minutes."));
        Mockito.verify(ServerProvider.getServer()).setIdleTimeout(50);
    }

    @Test
    public void testTabComplete() {
        MatcherAssert.assertThat(command.tabComplete(null, null, null), is(Collections.emptyList()));
        MatcherAssert.assertThat(command.tabComplete(sender, "", new String[0]), is(Collections.emptyList()));
        MatcherAssert.assertThat(command.tabComplete(sender, "", new String[]{"12"}),
            is(Collections.emptyList()));
        MatcherAssert.assertThat(command.tabComplete(sender, "", new String[]{"12", "test"}),
            is(Collections.emptyList()));
    }
}
