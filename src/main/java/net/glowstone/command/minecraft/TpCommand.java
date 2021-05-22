package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Differs from {@link TeleportCommand} in that all relative coordinates are relative to the target
 * rather than the sender. /teleport and /tp were synonyms until Minecraft 1.10.
 */
public class TpCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TpCommand() {
        super("tp");
        setPermission("minecraft.command.tp"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        switch (args.length) {
            case 0:
                sendUsageMessage(sender, commandMessages);
                return false;
            case 1:
                Entity from;
                if (sender instanceof Player) {
                    from = (Entity) sender;
                } else {
                    sender.sendMessage(ChatColor.RED + "Only entities can be teleported");
                    return false;
                }
                String name = args[0];
                if (name.startsWith("@") && !name.startsWith("@e") && name.length() >= 2
                    && CommandUtils.isPhysical(sender)) {
                    Location location = from.getLocation();
                    CommandTarget target = new CommandTarget(sender, name);
                    Entity[] matched = target.getMatched(location);
                    if (matched.length == 0) {
                        commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                            .sendInColor(ChatColor.RED, sender, name);
                        return false;
                    }
                    for (Entity entity : matched) {
                        from.teleport(entity);
                        sender.sendMessage(
                            "Teleported " + CommandUtils.getName(from) + " to " + CommandUtils
                                .getName(entity));
                    }
                    return true;
                } else {
                    Player player = Bukkit.getPlayerExact(name);
                    if (player == null) {
                        commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                            .sendInColor(ChatColor.RED, sender, name);
                        return false;
                    } else {
                        from.teleport(player);
                        sender.sendMessage("Teleported " + CommandUtils.getName(from) + " to "
                            + player.getName());
                        return true;
                    }
                }
            case 2:
                Entity destination;
                String fromName = args[0];
                String destName = args[1];
                if (fromName.startsWith("@") && fromName.length() >= 2 && CommandUtils
                    .isPhysical(sender)) {
                    Location location = CommandUtils.getLocation(sender);
                    CommandTarget target = new CommandTarget(sender, fromName);
                    Entity[] matched = target.getMatched(location);
                    if (matched.length == 0) {
                        commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                            .sendInColor(ChatColor.RED, sender, fromName);
                        return false;
                    }
                    for (Entity entity : matched) {
                        if (destName.startsWith("@") && !destName.startsWith("@e")
                            && destName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                            Location location2 = CommandUtils.getLocation(sender);
                            CommandTarget target2 = new CommandTarget(sender, destName);
                            Entity[] matched2 = target2.getMatched(location2);
                            if (matched2.length == 0) {
                                commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                                    .sendInColor(ChatColor.RED, sender,
                                        destName);
                                return false;
                            }
                            destination = matched2[0];
                        } else {
                            Player player = Bukkit.getPlayerExact(destName);
                            if (player == null) {
                                commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                                    .sendInColor(ChatColor.RED, sender,
                                        destName);
                                return false;
                            } else {
                                destination = player;
                            }
                        }
                        entity.teleport(destination);
                        sender.sendMessage(
                            "Teleported " + CommandUtils.getName(entity) + " to " + CommandUtils
                                .getName(destination));
                    }
                    return true;
                } else {
                    Player player = Bukkit.getPlayerExact(fromName);
                    if (player == null) {
                        sender
                            .sendMessage(
                                ChatColor.RED + "Player '" + fromName + "' is not online");
                        return false;
                    } else {
                        if (destName.startsWith("@") && !destName.startsWith("@e")
                            && destName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                            Location location2 = CommandUtils.getLocation(sender);
                            CommandTarget target2 = new CommandTarget(sender, destName);
                            Entity[] matched2 = target2.getMatched(location2);
                            if (matched2.length == 0) {
                                commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                                    .sendInColor(ChatColor.RED, sender,
                                        destName);
                                return false;
                            }
                            destination = matched2[0];
                        } else {
                            Player player2 = Bukkit.getPlayerExact(destName);
                            if (player2 == null) {
                                commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                                    .sendInColor(ChatColor.RED, sender,
                                        destName);
                                return false;
                            } else {
                                destination = player2;
                            }
                        }
                    }
                    player.teleport(destination);
                    sender.sendMessage("Teleported " + player.getName() + " to " + CommandUtils
                        .getName(destination));
                    return true;
                }
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
                return new Entity[] {player};
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
                entities = new Entity[] {(Entity) sender};
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
