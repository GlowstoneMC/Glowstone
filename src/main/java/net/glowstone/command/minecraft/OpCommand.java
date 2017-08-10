package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class OpCommand extends VanillaCommand {
    public OpCommand() {
        super("op", GlowServer.lang.getString("command.minecraft.op.description"), GlowServer.lang.getString("command.minecraft.op.usage"), Collections.emptyList());
        setPermission("minecraft.command.op");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.generic.usage", GlowServer.lang.getString(sender, "command.minecraft.op.usage")));
            return false;
        }
        String name = args[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        player.setOp(true);
        sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.op.opped", player.getName()));
        return true;
    }
}
