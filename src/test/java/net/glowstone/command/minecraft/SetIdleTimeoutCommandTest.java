package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.command.CommandTest;
import org.bukkit.ChatColor;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class SetIdleTimeoutCommandTest extends CommandTest<SetIdleTimeoutCommand> {

    public SetIdleTimeoutCommandTest() {
        super(SetIdleTimeoutCommand::new);
    }

    @Before
    @Override
    public void before() {
        super.before();
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
            + "Can't set an idle timeout of -42 minutes: the minimum is 1 minute."));
    }

    @Test
    public void testExecuteFailsWithNullTimeout() {
        final boolean commandResult = command.execute(opSender, "label", new String[]{"0"});

        MatcherAssert.assertThat(commandResult, is(false));
        Mockito.verify(opSender).sendMessage(eq(ChatColor.RED
            + "Can't set an idle timeout of 0 minutes: the minimum is 1 minute."));
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
