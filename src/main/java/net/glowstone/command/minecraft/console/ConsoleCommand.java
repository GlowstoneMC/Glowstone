package net.glowstone.command.minecraft.console;

import net.glowstone.ConsoleManager.ColoredCommandSender;
import net.glowstone.i18n.LocalizedStrings;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.List;

public abstract class ConsoleCommand extends VanillaCommand {

    protected ConsoleCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof ColoredCommandSender) {
            return innerExecute((ColoredCommandSender) sender, commandLabel, args);
        } else {
            LocalizedStrings.Glowstone.Command.Error.CONSOLE_ONLY.send(sender);
            return true;
        }
    }

    /**
     * Executes the command.
     *
     * @param sender Console object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args All arguments passed to the command, split via ' '
     * @return false if the command failed and a usage message should be printed, otherwise true
     */
    protected abstract boolean innerExecute(
            ColoredCommandSender sender, String commandLabel, String[] args);
}
