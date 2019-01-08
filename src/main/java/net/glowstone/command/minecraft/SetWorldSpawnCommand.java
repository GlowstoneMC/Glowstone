package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class SetWorldSpawnCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SetWorldSpawnCommand() {
        super("setworldspawn");
        setPermission("minecraft.command.setworldspawn"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        Location spawnLocation;

        final World world = CommandUtils.getWorld(sender);

        if (args.length == 0) { // Get the player current location
            if (CommandUtils.isPhysical(sender)) {
                spawnLocation = sender instanceof Entity ? ((Entity) sender).getLocation()
                        : ((BlockCommandSender) sender).getBlock().getLocation();
            } else {
                sender.sendMessage(ChatColor.RED
                        + "Default coordinates can not be used without a physical user.");
                return false;
            }
        } else if (args.length >= 3) { // manage arguments
            final Location senderLocation;

            // Get the sender coordinates if relative is used
            if (args[0].startsWith("~") || args[1].startsWith("~") || args[2].startsWith("~")) {
                if (!CommandUtils.isPhysical(sender)) {
                    sender.sendMessage(ChatColor.RED
                            + "Relative coordinates can not be used without a physical user.");
                    return false;
                } else {
                    senderLocation = sender instanceof Entity ? ((Entity) sender).getLocation()
                            : ((BlockCommandSender) sender).getBlock().getLocation();
                }
            } else { // Otherwise, the current location can be set to 0/0/0 (since it's absolute)
                senderLocation = new Location(world, 0, 0, 0);
            }

            spawnLocation = CommandUtils.getLocation(senderLocation, args[0], args[1], args[2]);
        } else {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        if (spawnLocation.getBlockY() < 0) {
            sender.sendMessage(ChatColor.RED + "The y coordinate (" + spawnLocation.getBlockY()
                    + ") is too small, it must be at least 0.");
            return false;
        } else if (spawnLocation.getBlockY() > world.getMaxHeight()) {
            sender.sendMessage(ChatColor.RED + "'" + spawnLocation.getBlockY()
                    + "' is too high for the current world. Max value is '" + world.getMaxHeight()
                    + "'.");
            return false;
        }

        world.setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(),
                spawnLocation.getBlockZ());
        sender.sendMessage(
                "Set world spawn point to " + spawnLocation.getBlockX() + ", " + spawnLocation
                        .getBlockY() + ", " + spawnLocation.getBlockZ() + ".");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
