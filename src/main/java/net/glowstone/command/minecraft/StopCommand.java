package net.glowstone.command.minecraft;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class StopCommand extends VanillaCommand {

    public StopCommand() {
        super("stop", "Gracefully stops the server.", "/stop", Collections.emptyList());
        setPermission("minecraft.command.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        Bukkit.shutdown();
        return true;
    }
}
