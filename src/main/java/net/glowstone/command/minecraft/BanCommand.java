package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class BanCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public BanCommand() {
        super("ban", "Bans a player from the server.", "/ban <player> [reason]",
                Collections.emptyList());
        setPermission("minecraft.command.ban");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length > 0) {
            String name = args[0];
            GlowServer server = (GlowServer) ServerProvider.getServer();
            // asynchronously lookup player
            server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
                if (ex != null) {
                    sender.sendMessage(ChatColor.RED + "Failed to ban " + name + ": "
                            + ex.getMessage());
                    ex.printStackTrace();
                    return;
                }
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Could not ban player " + args[0]);
                    return;
                }
                if (args.length == 1) {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(),
                            null, null, null);
                } else {
                    StringBuilder reason = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(),
                            reason.toString(), null, null);
                }
                sender.sendMessage("Banned player " + player.getName());
            });
            // todo: asynchronous command callbacks?
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length == 1) {
            super.tabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
