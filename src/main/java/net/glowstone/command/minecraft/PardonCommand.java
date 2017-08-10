package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class PardonCommand extends VanillaCommand {
    public PardonCommand() {
        super("pardon", I.tr("command.minecraft.pardon.description"), I.tr("command.minecraft.pardon.usage"), Collections.emptyList());
        setPermission("minecraft.command.pardon");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.pardon.usage")));
            return false;
        }
        String name = args[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
        if (!banList.isBanned(player.getName())) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.pardon.failed", name));
            return false;
        }
        banList.pardon(player.getName());
        sender.sendMessage(I.tr(sender, "command.minecraft.pardon.success", name));
        return true;
    }
}
