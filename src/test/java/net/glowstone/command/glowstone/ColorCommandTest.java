package net.glowstone.command.glowstone;

import net.glowstone.command.CommandTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ColorCommandTest extends CommandTest<ColorCommand> {

    // TODO: Add more tests.

    public ColorCommandTest() {
        super(ColorCommand::new);
    }

    @Test
    @Override
    public void testExecuteWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(true));
    }
}

