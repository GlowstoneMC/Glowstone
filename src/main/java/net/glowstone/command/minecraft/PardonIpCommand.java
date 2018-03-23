package net.glowstone.command.minecraft;

import com.google.common.net.InetAddresses;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class PardonIpCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public PardonIpCommand() {
        super("pardon-ip", "Unbans an IP address from the server.", "/pardon-ip <address>",
            Collections.emptyList());
        setPermission("minecraft.command.pardon-ip");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String ip = args[0];
        if (!InetAddresses.isInetAddress(ip)) {
            sender.sendMessage(ChatColor.RED + "You have entered an invalid IP address");
            return false;
        }
        GlowServerProvider.getServer().unbanIP(ip);
        sender.sendMessage("Unbanned IP address " + ip);
        return true;
    }
}
