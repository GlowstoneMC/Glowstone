package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;
import com.google.common.net.InetAddresses;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class PardonIpCommand extends VanillaCommand {
    public PardonIpCommand() {
        super("pardon-ip", GlowServer.lang.getString("command.minecraft.pardonip.description"), GlowServer.lang.getString("command.minecraft.pardonip.usage"), Collections.emptyList());
        setPermission("minecraft.command.pardon-ip");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.generic.usage", GlowServer.lang.getString(sender, "command.minecraft.pardonip.usage")));
            return false;
        }
        String ip = args[0];
        if (!InetAddresses.isInetAddress(ip)) {
            sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.minecraft.pardonip.invalid"));
            return false;
        }
        Bukkit.getServer().unbanIP(ip);
        sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.pardonip.success", ip));
        return true;
    }
}
