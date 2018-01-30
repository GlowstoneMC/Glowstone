package net.glowstone.command.minecraft;

import java.util.Collections;
import net.glowstone.GlowServer;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class PardonCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public PardonCommand() {
        super("pardon", "Unbans a player from the server.", "/pardon <name>",
            Collections.emptyList());
        setPermission("minecraft.command.pardon");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String name = args[0];
        GlowServer server = (GlowServer) Bukkit.getServer();
        // asynchronously lookup player
        server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
            if (ex != null) {
                sender.sendMessage(ChatColor.RED + "Failed to unban " + name + ": "
                        + ex.getMessage());
                ex.printStackTrace();
                return;
            }
            BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
            if (!banList.isBanned(player.getName())) {
                sender.sendMessage(ChatColor.RED + "Could not unban player " + player.getName()
                        + ": not banned");
                return;
            }
            banList.pardon(player.getName());
            sender.sendMessage("Unbanned player " + name);
        });
        // todo: asynchronous command callbacks?
        return true;
    }
}
