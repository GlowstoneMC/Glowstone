package net.glowstone.command.console;

import java.util.Collections;
import net.glowstone.ConsoleManager;
import net.glowstone.i18n.GlowstoneMessages;

public class BindCommand extends ConsoleCommand {

    /**
     * Creates the instance for this command.
     */
    public BindCommand() {
        super("!bind",
                GlowstoneMessages.Command.Error.NOT_YET_IMPLEMENTED.get(),
                GlowstoneMessages.Command.Error.NOT_YET_IMPLEMENTED.get(),
                Collections.emptyList());
    }

    @Override
    protected boolean executeOnConsole(
            ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
        GlowstoneMessages.Command.Error.NOT_YET_IMPLEMENTED.send(sender);
        return true;
    }
}
