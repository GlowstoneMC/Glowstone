package net.glowstone.command.glowstone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import net.glowstone.command.CommandTest;

public class ColorCommandTest extends CommandTest<ColorCommand> {

    @Override
    public void testExecuteWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(true));
    }
    // TODO: Add more tests.

    public ColorCommandTest() {
        super(ColorCommand::new);
    }
}

