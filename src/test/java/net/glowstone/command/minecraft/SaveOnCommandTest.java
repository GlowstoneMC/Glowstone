package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTest;

public class SaveOnCommandTest extends CommandTest<SaveToggleCommand> {

    // TODO: Add more tests.

    public SaveOnCommandTest() {
        super(() -> new SaveToggleCommand(true));
    }
}

