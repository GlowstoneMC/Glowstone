package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowVanillaCommand;
import net.glowstone.i18n.LocalizedStringImpl;
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
                commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL_COORDS)
                        .sendInColor(ChatColor.RED, sender);
                return false;
            }
        } else if (args.length >= 3) { // manage arguments
            final Location senderLocation;

            // Get the sender coordinates if relative is used
            if (args[0].startsWith("~") || args[1].startsWith("~") || args[2].startsWith("~")) {
                if (!CommandUtils.isPhysical(sender)) {
                    commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL_COORDS)
                            .sendInColor(ChatColor.RED, sender);
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

        int newY = spawnLocation.getBlockY();
        if (newY < 0) {
            commandMessages.getGeneric(GenericMessage.TOO_LOW).sendInColor(ChatColor.RED, sender);
            return false;
        } else if (newY > world.getMaxHeight()) {
            commandMessages.getGeneric(GenericMessage.TOO_HIGH)
                    .sendInColor(ChatColor.RED, sender, world.getMaxHeight());
            return false;
        }

        int newX = spawnLocation.getBlockX();
        int newZ = spawnLocation.getBlockZ();
        world.setSpawnLocation(newX, newY,
                newZ);
        new LocalizedStringImpl("setworldspawn.done", commandMessages.getResourceBundle())
                .send(sender, newX, newY, newZ);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
