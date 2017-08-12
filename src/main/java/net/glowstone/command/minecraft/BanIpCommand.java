package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import com.google.common.net.InetAddresses;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BanIpCommand extends VanillaCommand {
    public BanIpCommand() {
        super("ban-ip", I.tr("command.minecraft.ban-ip.description"), I.tr("command.minecraft.ban-ip.usage"), Collections.emptyList());
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
                sender.sendMessage(I.tr(sender, "command.minecraft.ban-ip.banned", target));
                return true;
            }
            sender.sendMessage(I.tr(sender, "command.minecraft.ban-ip.invalid"));
            return false;
        }
        sender.sendMessage(I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.ban-ip.usage")));
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
