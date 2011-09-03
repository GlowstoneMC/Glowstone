package net.glowstone.command;

import java.util.Collection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissionDefault;

/**
 * A command to print a list of other built-in commands.
 */
public class HelpCommand extends GlowCommand {
    
    private final Collection<? extends Command> commands;

    public HelpCommand(GlowServer server, Collection<? extends Command> commands) {
        super(server, "help", "Display help on built-in commands.", "");
        this.commands = commands;
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        for (Command command : commands) {
            sender.sendMessage(ChatColor.WHITE + command.getUsage() + ChatColor.GRAY + " - " + command.getDescription());
        }
        return true;
    }
    
    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.TRUE;
    }
}
