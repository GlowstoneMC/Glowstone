package net.glowstone.command.console;

import java.util.Collections;
import net.glowstone.ConsoleManager;
import net.glowstone.i18n.LocalizedStrings;

public class BindCommand extends ConsoleCommand {

    /**
     * Creates the instance for this command.
     */
    public BindCommand() {
        super("!bind",
                LocalizedStrings.Glowstone.Command.Error.NOT_YET_IMPLEMENTED.get(),
                LocalizedStrings.Glowstone.Command.Error.NOT_YET_IMPLEMENTED.get(),
                Collections.emptyList());
    }

    @Override
    protected boolean innerExecute(
            ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
        LocalizedStrings.Glowstone.Command.Error.NOT_YET_IMPLEMENTED.send(sender);
        return true;
    }
}
