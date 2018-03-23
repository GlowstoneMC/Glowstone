package net.glowstone.command.minecraft;

import java.util.Collections;
import net.glowstone.GlowServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class OpCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public OpCommand() {
        super("op", "Turns a player into a server operator.", "/op <player>",
                Collections.emptyList());
        setPermission("minecraft.command.op");
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
        GlowServer server = (GlowServer) GlowServerProvider.getServer();
        // asynchronously lookup player
        server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
            if (ex != null) {
                sender.sendMessage(ChatColor.RED + "Failed to op " + name + ": "
                        + ex.getMessage());
                ex.printStackTrace();
                return;
            }
            player.setOp(true);
            sender.sendMessage("Opped " + player.getName());
        });
        // todo: asynchronous command callbacks?
        return true;
    }
}
