package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class ToggleDownfallCommand extends VanillaCommand {

    public ToggleDownfallCommand() {
        super("toggledownfall", "Toggles the weather.", "/toggledownfall", Collections.emptyList());
        setPermission("minecraft.command.toggledownfall");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        final World world = CommandUtils.getWorld(sender);

        world.setThundering(!world.hasStorm());
        world.setStorm(!world.hasStorm());
        sender.sendMessage("Toggled downfall");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
