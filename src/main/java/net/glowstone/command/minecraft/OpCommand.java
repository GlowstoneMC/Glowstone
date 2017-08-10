package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class OpCommand extends VanillaCommand {
    public OpCommand() {
        super("op", I.tr("command.minecraft.op.description"), I.tr("command.minecraft.op.usage"), Collections.emptyList());
        setPermission("minecraft.command.op");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.op.usage")));
            return false;
        }
        String name = args[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        player.setOp(true);
        sender.sendMessage(I.tr(sender, "command.minecraft.op.opped", player.getName()));
        return true;
    }
}
