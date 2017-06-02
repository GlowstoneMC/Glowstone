package net.glowstone.command.minecraft;

import com.google.common.collect.ImmutableList;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;
import java.util.List;

public class BanCommand extends BukkitCommand {

    public BanCommand() {
        super("ban", "Bans a player from the server.", "/ban <player> [reason]", Collections.emptyList());
        setPermission("minecraft.command.ban");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length > 0) {
            if (args.length == 1) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], null, null, null);
            } else {
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], reason.toString(), null, null);
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
