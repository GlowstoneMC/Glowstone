package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTest;

public class SaveOffCommandTest extends CommandTest<SaveToggleCommand> {

    // TODO: Add more tests.

    public SaveOffCommandTest() {
        super(() -> new SaveToggleCommand(false));
    }
}

