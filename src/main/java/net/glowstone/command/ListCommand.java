package net.glowstone.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

/**
 * A built-in command to remove a player's OP status.
 */
public class ListCommand extends GlowCommand {
    
    public ListCommand(GlowServer server) {
        super(server, "list", "List players online.", "<player>");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        String result = "";
        for (Player p : server.getOnlinePlayers()) {
            if (result.length() > 0) result += ", ";
            result += p.getName();
        }
        sender.sendMessage(ChatColor.GRAY + "Players online (" + server.getOnlinePlayers().length + "): " + result);
        return true;
    }
    
    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
