package net.glowstone.command.minecraft.console;

import net.glowstone.ConsoleManager;
import net.glowstone.i18n.LocalizedStrings;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Collections;

public class BindCommand extends ConsoleCommand {
    public BindCommand() {
        super("bind",
                LocalizedStrings.Glowstone.Command.Error.NOT_YET_IMPLEMENTED.get(),
                LocalizedStrings.Glowstone.Command.Error.NOT_YET_IMPLEMENTED.get(),
                Collections.emptyList());
    }

    @Override
    protected boolean innerExecute(ConsoleManager.ColoredCommandSender sender, String commandLabel, String[] args) {
        LocalizedStrings.Glowstone.Command.Error.NOT_YET_IMPLEMENTED.send(sender);
        return true;
    }
}
