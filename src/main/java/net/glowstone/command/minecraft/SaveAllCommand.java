package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class SaveAllCommand extends VanillaCommand {

    public SaveAllCommand() {
        super("save-all", "Saves the server to disk.", "/save-all", Collections.emptyList());
        setPermission("minecraft.command.save-all");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        sender.sendMessage("Saving...");
        for (World world : sender.getServer().getWorlds()) {
            world.save();
            sender.sendMessage("Saved world '" + world.getName() + "'");
        }
        sender.sendMessage("Saved the worlds");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
