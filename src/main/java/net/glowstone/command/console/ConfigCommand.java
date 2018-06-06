package net.glowstone.command.console;

import java.util.Collections;
import net.glowstone.ConsoleManager;

public class ConfigCommand extends ConsoleCommand {

    /**
     * Creates the instance for this command.
     */
    public ConfigCommand() {
        super("config",
                "Gets or sets an option or variable for the console's line reader",
                "config <name> <value> OR config <name>",
                Collections.emptyList());
    }

    @Override
    protected boolean innerExecute(
            ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
        switch (args.length) {
            case 0:
                return false;
            case 1:
                // get
                sender.sendMessage(args[0] + " = " + sender.getLineReaderOption(args[0]));
                return true;
            default:
                // set
                Object value;
                if (args[0].endsWith("L")) {
                    value = Long.parseLong(args[1]
                            .substring(0, args[1].length() - 1));
                } else {
                    try {
                        value = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        if (args[1].equalsIgnoreCase("true")
                                || args[1].equalsIgnoreCase("false")) {
                            value = Boolean.parseBoolean(args[1]);
                        } else {
                            value = args[1];
                        }
                    }
                }
                try {
                    sender.setLineReaderOption(args[0], value);
                    return true;
                } catch (IllegalArgumentException e) {
                    // TODO: Proper error message
                    return false;
                }
        }
    }
}
