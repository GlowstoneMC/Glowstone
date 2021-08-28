package net.glowstone.command.minecraft;

import com.google.common.collect.ImmutableList;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpawnPointCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SpawnPointCommand() {
        super("spawnpoint");
        setPermission("minecraft.command.spawnpoint"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (args.length != 0 && args.length != 1 && args.length < 4) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        final String playerPattern = args.length >= 1 ? args[0] : null;
        List<Player> targets;
        Location spawnLocation;

        // Manage player(s)
        if (playerPattern == null) { // Default player, set to current one (if a player)
            if (sender instanceof Player) {
                targets = ImmutableList.of((Player) sender);
            } else {
                new LocalizedStringImpl("spawnpoint.no-player",
                    commandMessages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender);
                return false;
            }
        } else if (playerPattern.startsWith("@") && playerPattern.length() > 1 && CommandUtils
            .isPhysical(sender)) { // Manage selectors
            final Location location = sender instanceof Entity ? ((Entity) sender).getLocation()
                : ((BlockCommandSender) sender).getBlock().getLocation();
            final Entity[] entities = new CommandTarget(sender, args[0]).getMatched(location);
            targets = new ArrayList<>(entities.length);

            for (final Entity entity : entities) {
                if (entity instanceof Player) {
                    targets.add((Player) entity);
                }
            }
        } else { // One player given (with or without coordinates)
            final Player player = Bukkit.getPlayerExact(playerPattern);

            if (player == null) {
                commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                    .sendInColor(ChatColor.RED, sender, playerPattern);
                return false;
            } else {
                targets = Collections.singletonList(player);
            }
        }

        // Manage coordinates
        if (args.length == 4) { // Coordinates are given
            final Location currentLocation;

            final World world = CommandUtils.getWorld(sender);

            // If we are using relative coordinates, we need to get the sender location
            if (args[1].startsWith("~") || args[2].startsWith("~") || args[3].startsWith("~")) {
                if (!CommandUtils.isPhysical(sender)) {
                    commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL_COORDS)
                        .sendInColor(ChatColor.RED, sender);
                    return false;
                } else {
                    currentLocation = sender instanceof Entity ? ((Entity) sender).getLocation()
                        : ((BlockCommandSender) sender).getBlock().getLocation();
                }
            } else { // Otherwise, the current location can be set to 0/0/0 (since it's absolute)
                currentLocation = new Location(world, 0, 0, 0);
            }

            spawnLocation = CommandUtils.getLocation(currentLocation, args[1], args[2], args[3]);

            if (spawnLocation.getY() < 0) {
                commandMessages.getGeneric(GenericMessage.TOO_LOW)
                    .sendInColor(ChatColor.RED, sender);
                return false;
            } else if (spawnLocation.getBlockY() > world.getMaxHeight()) {
                commandMessages.getGeneric(GenericMessage.TOO_HIGH)
                    .sendInColor(ChatColor.RED, sender, world.getMaxHeight());
                return false;
            }
        } else { // Use the sender coordinates
            if (CommandUtils.isPhysical(sender)) {
                spawnLocation = sender instanceof Entity ? ((Entity) sender).getLocation()
                    : ((BlockCommandSender) sender).getBlock().getLocation();
            } else {
                commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL_COORDS)
                    .sendInColor(ChatColor.RED, sender);
                return false;
            }
        }

        // Update spawn location
        LocalizedStringImpl doneForOne = new LocalizedStringImpl("spawnpoint.done",
            commandMessages.getResourceBundle());
        int newX = spawnLocation.getBlockX();
        int newY = spawnLocation.getBlockY();
        int newZ = spawnLocation.getBlockZ();
        for (final Player target : targets) {
            target.setBedSpawnLocation(spawnLocation, true);
            doneForOne.send(sender, target.getName(), newX, newY, newZ);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return args.length == 1 ? super.tabComplete(sender, alias, args) : Collections.emptyList();
    }
}
