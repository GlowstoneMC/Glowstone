package net.glowstone.command.console;

import java.util.Collections;
import net.glowstone.ConsoleManager;

public class KeyMapCommand extends ConsoleCommand {

    /**
     * Creates the instance for this command.
     */
    public KeyMapCommand() {
        super("!keymap",
                "Gets or sets the keymap for this console's line reader",
                "!keymap OR !keymap <map>",
                Collections.emptyList());
    }

    @Override
    protected boolean executeOnConsole(
            ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
        if (args.length > 0 && !sender.getLineReader().setKeyMap(args[0])) {
            return false;
        }
        sender.sendMessage(sender.getLineReader().getKeyMap());
        return true;
    }
}
