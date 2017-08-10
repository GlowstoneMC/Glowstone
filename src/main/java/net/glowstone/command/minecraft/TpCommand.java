package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TpCommand extends VanillaCommand {
    public TpCommand() {
        super("tp", I.tr("command.minecraft.tp.description"), I.tr("command.minecraft.tp.usage"), Collections.emptyList());
        setPermission("minecraft.command.tp");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.tp.usage")));
            return false;
        }
        if (args.length <= 2) {
            if (args.length == 1) {
                Entity from;
                if (sender instanceof Player) {
                    from = (Entity) sender;
                } else {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.tp.entity"));
                    return false;
                }
                String name = args[0];
                if (name.startsWith("@") && !name.startsWith("@e") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
                    Location location = from.getLocation();
                    CommandTarget target = new CommandTarget(sender, name);
                    Entity[] matched = target.getMatched(location);
                    if (matched.length == 0) {
                        sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.selector", name));
                        return false;
                    }
                    for (Entity entity : matched) {
                        from.teleport(entity);
                        sender.sendMessage(I.tr(sender, "command.minecraft.tp.teleported", CommandUtils.getName(from), CommandUtils.getName(entity)));
                    }
                    return true;
                } else {
                    Player player = Bukkit.getPlayerExact(name);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.player.offline", name));
                        return false;
                    } else {
                        from.teleport(player);
                        sender.sendMessage(I.tr(sender, "command.minecraft.tp.teleported",CommandUtils.getName(from), player.getName()));
                        return true;
                    }
                }
            } else {
                Entity destination;
                String fromName = args[0], destName = args[1];
                if (fromName.startsWith("@") && fromName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                    Location location = CommandUtils.getLocation(sender);
                    CommandTarget target = new CommandTarget(sender, fromName);
                    Entity[] matched = target.getMatched(location);
                    if (matched.length == 0) {
                        sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.selector", fromName));
                        return false;
                    }
                    for (Entity entity : matched) {
                        if (destName.startsWith("@") && !destName.startsWith("@e") && destName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                            Location location2 = CommandUtils.getLocation(sender);
                            CommandTarget target2 = new CommandTarget(sender, destName);
                            Entity[] matched2 = target2.getMatched(location2);
                            if (matched2.length == 0) {
                                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.selector", destName));
                                return false;
                            }
                            destination = matched2[0];
                        } else {
                            Player player = Bukkit.getPlayerExact(destName);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.player.offline", destName));
                                return false;
                            } else {
                                destination = player;
                            }
                        }
                        entity.teleport(destination);
                        sender.sendMessage(I.tr(sender, "command.minecraft.tp.teleported", CommandUtils.getName(entity), CommandUtils.getName(destination)));
                    }
                    return true;
                } else {
                    Player player = Bukkit.getPlayerExact(fromName);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.player.offline", fromName));
                        return false;
                    } else {
                        if (destName.startsWith("@") && !destName.startsWith("@e") && destName.length() >= 2 && CommandUtils.isPhysical(sender)) {
                            Location location2 = CommandUtils.getLocation(sender);
                            CommandTarget target2 = new CommandTarget(sender, destName);
                            Entity[] matched2 = target2.getMatched(location2);
                            if (matched2.length == 0) {
                                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.selector", destName));
                                return false;
                            }
                            destination = matched2[0];
                        } else {
                            Player player2 = Bukkit.getPlayerExact(destName);
                            if (player2 == null) {
                                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.player.offline", destName));
                                return false;
                            } else {
                                destination = player;
                            }
                        }
                    }
                    player.teleport(destination);
                    sender.sendMessage(I.tr(sender, "command.minecraft.tp.teleported", player.getName(), CommandUtils.getName(destination)));
                    return true;
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Coordinate-based teleporting is not supported yet!");
            return false;
        }
    }
}
