package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import com.google.common.net.InetAddresses;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class PardonIpCommand extends VanillaCommand {
    public PardonIpCommand() {
        super("pardon-ip", I.tr("command.minecraft.pardon-ip.description"), I.tr("command.minecraft.pardon-ip.usage"), Collections.emptyList());
        setPermission("minecraft.command.pardon-ip");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.pardon-ip.usage")));
            return false;
        }
        String ip = args[0];
        if (!InetAddresses.isInetAddress(ip)) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.pardon-ip.invalid"));
            return false;
        }
        Bukkit.getServer().unbanIP(ip);
        sender.sendMessage(I.tr(sender, "command.minecraft.pardon-ip.success", ip));
        return true;
    }
}
