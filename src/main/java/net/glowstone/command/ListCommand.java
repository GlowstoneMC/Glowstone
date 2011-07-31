package net.glowstone.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;

/**
 * A built-in command to remove a player's OP status.
 */
public class ListCommand extends GlowCommand {
    
    public ListCommand(GlowServer server) {
        super(server, "list", "List players online.", "<player>");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!checkOp(sender)) {
            return false;
        } else {
            String result = "";
            for (Player p : server.getOnlinePlayers()) {
                if (result.length() > 0) result += ", ";
                result += p.getName();
            }
            sender.sendMessage(ChatColor.GRAY + "Players online (" + server.getOnlinePlayers().length + "): " + result);
            return true;
        }
    }
    
}
