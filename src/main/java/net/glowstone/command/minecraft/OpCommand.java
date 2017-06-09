package net.glowstone.command.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class OpCommand extends VanillaCommand {
    public OpCommand() {
        super("op", "Turns a player into a server operator.", "/op <player>", Collections.emptyList());
        setPermission("minecraft.command.op");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String name = args[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        player.setOp(true);
        sender.sendMessage("Opped " + player.getName());
        return true;
    }
}
