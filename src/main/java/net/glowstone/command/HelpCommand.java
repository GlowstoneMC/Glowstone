package net.glowstone.command;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;

/**
 * A command to print a list of other built-in commands.
 */
public class HelpCommand extends GlowCommand {
    
    private final List<Command> commands;

    public HelpCommand(GlowServer server, List<Command> commands) {
        super(server, "help", "Display help on built-in commands.", "");
        this.commands = commands;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!checkOp(sender)) {
            return false;
        } else {
            for (Command command : commands) {
                sender.sendMessage(ChatColor.WHITE + command.getUsage() + ChatColor.GRAY + " - " + command.getDescription());
            }
            return true;
        }
    }
    
}
