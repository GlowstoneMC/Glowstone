package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;
import net.glowstone.entity.meta.profile.PlayerProfile;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;
import java.util.List;

public class BanCommand extends VanillaCommand {

    public BanCommand() {
        super("ban", GlowServer.lang.getString("command.minecraft.ban.description"), "/ban " + GlowServer.lang.getString("command.minecraft.ban.args.player") + " " + GlowServer.lang.getString("command.minecraft.ban.args.reason"), Collections.emptyList());
        setPermission("minecraft.command.ban");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length > 0) {
            if (PlayerProfile.getProfile(args[0]) == null) {
                sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.minecraft.ban.failed", args[0]));
                return false;
            }
            if (args.length == 1) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], null, null, null);
            } else {
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], reason.toString(), null, null);
            }
            sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.ban.banned", args[0]));
            return true;
        }
        sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.generic.usage", "/ban " + GlowServer.lang.getString("command.minecraft.ban.args.player") + " " + GlowServer.lang.getString("command.minecraft.ban.args.reason")));
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
