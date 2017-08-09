package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;
import com.google.common.net.InetAddresses;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BanIpCommand extends VanillaCommand {
    public BanIpCommand() {
        super("ban-ip", GlowServer.lang.getString("command.minecraft.banip.description"), "/ban-ip " + GlowServer.lang.getString("command.minecraft.banip.args.target") + " " + GlowServer.lang.getString("command.minecraft.banip.args.reason"), Collections.emptyList());
        setPermission("minecraft.command.ban-ip");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length > 0) {
            String target = null;
            if (InetAddresses.isInetAddress(args[0])) {
                target = args[0];
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    target = player.getAddress().getAddress().getHostAddress();
                }
            }
            if (target != null) {
                if (args.length == 1) {
                    Bukkit.getBanList(BanList.Type.IP).addBan(target, null, null, null);
                } else {
                    StringBuilder reason = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                    Bukkit.getBanList(BanList.Type.IP).addBan(target, reason.toString(), null, null);
                }
                sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.banip.banned", target));
                return true;
            }
            sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.minecraft.banip.invalid"));
            return false;
        }
        sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.generic.usage", "/ban-ip " + GlowServer.lang.getString(sender, "command.minecraft.banip.args.target") + " " + GlowServer.lang.getString(sender, "command.minecraft.banip.args.reason")));
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            super.tabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
