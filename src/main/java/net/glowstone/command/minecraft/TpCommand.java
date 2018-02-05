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
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        if (args.length <= 2) {
            if (args.length == 1) {
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
                        sender.sendMessage(ChatColor.RED + "Selector " + name + " found nothing");
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
                        sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not online");
                        return false;
                    } else {
                        from.teleport(player);
                        sender.sendMessage(
                            "Teleported " + CommandUtils.getName(from) + " to " + player.getName());
                        return true;
                    }
                }
            } else {
                Entity destination;
                String fromName = args[0];
                String destName = args[1];
                if (fromName.startsWith("@") && fromName.length() >= 2 && CommandUtils
                    .isPhysical(sender)) {
                    Location location = CommandUtils.getLocation(sender);
                    CommandTarget target = new CommandTarget(sender, fromName);
                    Entity[] matched = target.getMatched(location);
                    if (matched.length == 0) {
                        sender
                            .sendMessage(ChatColor.RED + "Selector " + fromName + " found nothing");
                        return false;
                    }
                    for (Entity entity : matched) {
                        if (destName.startsWith("@") && !destName.startsWith("@e")
                            && destName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                            Location location2 = CommandUtils.getLocation(sender);
                            CommandTarget target2 = new CommandTarget(sender, destName);
                            Entity[] matched2 = target2.getMatched(location2);
                            if (matched2.length == 0) {
                                sender.sendMessage(
                                    ChatColor.RED + "Selector " + destName + " found nothing");
                                return false;
                            }
                            destination = matched2[0];
                        } else {
                            Player player = Bukkit.getPlayerExact(destName);
                            if (player == null) {
                                sender.sendMessage(
                                    ChatColor.RED + "Player '" + destName + "' is not online");
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
                            .sendMessage(ChatColor.RED + "Player '" + fromName + "' is not online");
                        return false;
                    } else {
                        if (destName.startsWith("@") && !destName.startsWith("@e")
                            && destName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                            Location location2 = CommandUtils.getLocation(sender);
                            CommandTarget target2 = new CommandTarget(sender, destName);
                            Entity[] matched2 = target2.getMatched(location2);
                            if (matched2.length == 0) {
                                sender.sendMessage(
                                    ChatColor.RED + "Selector " + destName + " found nothing");
                                return false;
                            }
                            destination = matched2[0];
                        } else {
                            Player player2 = Bukkit.getPlayerExact(destName);
                            if (player2 == null) {
                                sender.sendMessage(
                                    ChatColor.RED + "Player '" + destName + "' is not online");
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
            }
        } else {
            sender
                .sendMessage(ChatColor.RED + "Coordinate-based teleporting is not supported yet!");
            return false;
        }
    }
}
