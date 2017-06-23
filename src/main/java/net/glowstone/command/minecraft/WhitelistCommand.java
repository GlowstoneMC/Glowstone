package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;
import java.util.Set;

public class WhitelistCommand extends VanillaCommand {
    public WhitelistCommand() {
        super("whitelist", "Manage the server whitelist.", "/whitelist <on|off|list|add|remove|reload>", Collections.emptyList());
        setPermission("minecraft.command.whitelist");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String subcommand = args[0];
        if (subcommand.equals("on")) {
            sender.getServer().setWhitelist(true);
            sender.sendMessage("Turned on the whitelist");
            return true;
        }
        if (subcommand.equals("off")) {
            sender.getServer().setWhitelist(false);
            sender.sendMessage("Turned off the whitelist");
            return true;
        }
        if (subcommand.equals("list")) {
            Set<OfflinePlayer> whitelistedPlayers = sender.getServer().getWhitelistedPlayers();
            String[] names = new String[whitelistedPlayers.size()];
            int i = 0;
            for (OfflinePlayer p : whitelistedPlayers) {
                names[i++] = p.getName();
            }
            sender.sendMessage("There are " + names.length + " whitelisted players:");
            sender.sendMessage(CommandUtils.prettyPrint(names));
            return true;
        }
        if (subcommand.equals("add")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /whitelist add <player>");
                return false;
            }
            String name = args[1];
            OfflinePlayer player = sender.getServer().getOfflinePlayer(name);
            player.setWhitelisted(true);
            sender.sendMessage("Added " + name + " to the whitelist");
            return true;
        }
        if (subcommand.equals("remove")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /whitelist remove <player>");
                return false;
            }
            String name = args[1];
            OfflinePlayer player = sender.getServer().getOfflinePlayer(name);
            player.setWhitelisted(false);
            sender.sendMessage("Removed " + name + " to the whitelist");
            return true;
        }
        if (subcommand.equals("reload")) {
            sender.getServer().reloadWhitelist();
            sender.sendMessage("Reloaded the whitelist");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        return false;
    }
}
