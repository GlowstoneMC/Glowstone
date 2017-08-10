package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class KickCommand extends VanillaCommand {
    public KickCommand() {
        super("kick", GlowServer.lang.getString("command.minecraft.kick.description"), GlowServer.lang.getString("command.minecraft.kick.usage"), Collections.emptyList());
        setPermission("minecraft.command.kick");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.generic.usage", GlowServer.lang.getString(sender, "command.minecraft.kick.usage")));
            return false;
        }
        String playerName = args[0];
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.generic.offline", playerName));
            return false;
        }
        if (args.length == 1) {
            player.kickPlayer(null);
            sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.kick.kicked", player.getName()));
            return true;
        }
        String reason = StringUtils.join(args, ' ', 1, args.length);
        player.kickPlayer(reason);
        sender.sendMessage(GlowServer.lang.getString("command.minecraft.kick.reason", player.getName(), reason));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
