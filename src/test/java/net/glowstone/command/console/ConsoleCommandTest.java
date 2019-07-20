package net.glowstone.command.console;

import net.glowstone.command.CommandTest;
import org.mockito.Mockito;

import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;

public abstract class ConsoleCommandTest<T extends ConsoleCommand> extends CommandTest<T> {
    protected ConsoleCommandTest(Supplier<T> commandSupplier) {
        super(commandSupplier);
    }

    @Override
    public void testExecuteWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(true));
        Mockito.verify(sender).sendMessage(eq("This command can only be run from an admin console."));
    }
}
