package net.glowstone.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class StopCommand extends BukkitCommand {
    public StopCommand() {
        super("stop", "Gracefully stops the server", "/stop", Collections.emptyList());
        setPermission("minecraft.command.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        Bukkit.shutdown();
        return true;
    }
}
