package net.glowstone.command.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StopCommandTest extends CommandTest<StopCommand> {
    private GlowServer server;

    public StopCommandTest() {
        super(StopCommand::new);
    }

    @BeforeMethod
    @Override
    public void before() {
        super.before();
        server = Mockito.mock(GlowServer.class);
        ServerProvider.setMockServer(server);
        when(opSender.getServer()).thenReturn(server);
    }

    @Test
    public void testNoArgs() {
        assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(server).shutdown();
    }

    @Test
    public void testWithArgs() {
        assertThat(command.execute(opSender, "label",
                new String[]{"This", "is", "a", "custom", "shutdown", "message"}), is(true));
        Mockito.verify(server).shutdown("This is a custom shutdown message");
    }
}
