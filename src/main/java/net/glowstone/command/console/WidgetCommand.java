package net.glowstone.command.console;

import java.util.Collections;
import net.glowstone.ConsoleManager;

public class WidgetCommand extends ConsoleCommand {

    /**
     * Creates the instance for this command.
     */
    public WidgetCommand() {
        super("!widget",
                "Calls a widget on this console's line reader",
                "!widget <widget>",
                Collections.emptyList());
    }

    @Override
    protected boolean executeOnConsole(
            ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            return false;
        }
        try {
            sender.getLineReader().callWidget(args[1]);
            return true;
        } catch (Throwable t) {
            // TODO: Error message
            return false;
        }
    }
}
