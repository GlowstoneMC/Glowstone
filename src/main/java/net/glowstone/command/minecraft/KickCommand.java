package net.glowstone.command.minecraft;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class KickCommand extends VanillaCommand {
    public KickCommand() {
        super("kick", "Removes a player from the server.", "/kick <player> [reason]", Collections.emptyList());
        setPermission("minecraft.command.kick");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' is not online");
            return false;
        }
        if (args.length == 1) {
            player.kickPlayer(null);
            sender.sendMessage("Kicked " + player.getName());
            return true;
        }
        String reason = StringUtils.join(args, ' ', 1, args.length);
        player.kickPlayer(reason);
        sender.sendMessage("Kicked " + player.getName() + " for reason '" + reason + "'");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
