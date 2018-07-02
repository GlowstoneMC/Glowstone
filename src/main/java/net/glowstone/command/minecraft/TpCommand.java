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
            case 0:
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;

            case 1:
                return teleportSenderToEntity(sender, args[0]);

            case 2:
                return teleportEntityToEntity(sender, args[0], args[1]);

            default:
                sender.sendMessage(
                        ChatColor.RED + "Coordinate-based teleporting is not supported yet!");
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
}
