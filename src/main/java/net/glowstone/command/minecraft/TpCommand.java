package net.glowstone.command.minecraft;

import java.util.Collections;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TpCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TpCommand() {
        super("tp", "Teleports an entity to another entity or to specific coordinates.",
            "/tp [target entity] <destination player> "
                    + "OR /tp [target entity] <x> <y> <z> [<yaw> <pitch>]",
            Collections.emptyList());
        setPermission("minecraft.command.tp");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        switch (args.length) {
            case 1:
                return teleportSenderToEntity(sender, args[0]);

            case 2:
                return teleportEntityToEntity(sender, args[0], args[1]);

            case 3:
                return teleportToLocation(sender, null, args[0], args[1], args[2],
                        null,null);

            case 4:
                return teleportToLocation(sender, args[0], args[1], args[2], args[3],
                        null, null);

            case 5:
                return teleportToLocation(sender, null, args[0], args[1], args[2],
                        args[3], args[4]);

            case 6:
                return teleportToLocation(sender, args[0], args[1], args[2], args[3],
                        args[4], args[5]);

            default:
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
        }
    }

    private Entity[] matchEntities(CommandSender sender, String selector) {
        if (selector.startsWith("@") && CommandUtils.isPhysical(sender)) {
            Location location = CommandUtils.getLocation(sender);
            CommandTarget target = new CommandTarget(sender, selector);
            Entity[] matched = target.getMatched(location);
            if (matched.length == 0) {
                sender.sendMessage(
                        ChatColor.RED + "Selector " + selector + " found nothing");
            }
            return matched;
        } else {
            Player player = Bukkit.getPlayerExact(selector);
            if (player == null) {
                sender.sendMessage(
                        ChatColor.RED + "Player '" + selector + "' is not online");
                return new Entity[0];
            } else {
                return new Entity[] { player };
            }
        }
    }

    private boolean teleportSenderToEntity(CommandSender sender, String name) {
        Entity from;
        if (sender instanceof Player) {
            from = (Entity) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Only entities can be teleported");
            return false;
        }

        Entity[] matched = matchEntities(sender, name);

        if (matched.length == 0) {
            return false;
        } else {
            Entity destination = matched[0];

            from.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
            sender.sendMessage(
                    "Teleported " + CommandUtils.getName(from) + " to " + CommandUtils
                            .getName(destination));
            return true;
        }
    }

    private boolean teleportEntityToEntity(CommandSender sender, String fromName, String destName) {
        Entity[] matchedFrom = matchEntities(sender, fromName);
        Entity[] matchedDest = matchEntities(sender, destName);

        if (matchedDest.length == 0) {
            return false;
        } else {
            Entity destination = matchedDest[0];

            for (Entity entity : matchedFrom) {
                entity.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
                sender.sendMessage(
                        "Teleported " + CommandUtils.getName(entity) + " to " + CommandUtils
                                .getName(destination));
            }
            return true;
        }
    }

    private boolean teleportToLocation(CommandSender sender, String name,
                                       String x, String y, String z,
                                       String yaw, String pitch) {
        Entity[] entities;
        if (name == null) {
            if (sender instanceof Player) {
                entities = new Entity[] { (Entity) sender };
            } else {
                sender.sendMessage(ChatColor.RED + "Only entities can be teleported");
                return false;
            }
        } else {
            entities = matchEntities(sender, name);
            if (entities.length == 0) {
                return false;
            }
        }

        Location location = CommandUtils.getLocation(sender);
        location = CommandUtils.getLocation(location, x, y, z);
        if (yaw != null && pitch != null) {
            location = CommandUtils.getRotation(location, yaw, pitch);
        }

        for (Entity entity : entities) {
            entity.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
            sender.sendMessage(
                    "Teleported " + CommandUtils.getName(entity) + " to " + location.getX()
                            + ", " + location.getY() + ", " + location.getZ());
        }
        return true;
    }
}
