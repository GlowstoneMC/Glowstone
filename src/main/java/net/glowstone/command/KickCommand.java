package net.glowstone.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.glowstone.GlowServer;

/**
 * A built-in command to kick a person off the server.
 */
public class KickCommand extends GlowCommand {
    
    public KickCommand(GlowServer server) {
        super(server, "kick", "Kick a player off the server", "<player>");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 1)) {
            return false;
        } else if (!checkOp(sender)) {
            return false;
        } else {
            Player player = server.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.GRAY + "No such player " + args[0]);
                return false;
            }
            
            String senderName = (sender instanceof Player? ((Player) sender).getDisplayName(): "Console");
            player.kickPlayer("Kicked by " + sender);
            server.broadcastMessage(ChatColor.YELLOW + args[0] + " has been kicked");
            return true;
        }
    }
    
}