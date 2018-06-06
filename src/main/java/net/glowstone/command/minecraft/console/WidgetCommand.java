package net.glowstone.command.minecraft.console;

import net.glowstone.ConsoleManager;

import java.util.Collections;

public class WidgetCommand extends ConsoleCommand {
    public WidgetCommand() {
        super("widget",
                "Calls a widget on this console's line reader",
                "widget <widget>",
                Collections.emptyList());
    }

    @Override
    protected boolean innerExecute(ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
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
