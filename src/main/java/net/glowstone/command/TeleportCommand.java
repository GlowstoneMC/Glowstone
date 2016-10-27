package net.glowstone.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandTarget;
import org.bukkit.command.CommandUtils;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeleportCommand extends BukkitCommand {
    public TeleportCommand() {
        super("teleport",
                "Teleports entities to coordinates relative to the sender",
                "/teleport <target> <x> <y> <z> [<y-rot> <x-rot>]",
                Collections.emptyList());
        setPermission("glowstone.command.teleport");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 4 || args.length == 5) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by physical objects.");
            return false;
        }

        Player player = (Player) sender;
        Entity[] targets;
        if (args[0].startsWith("@")) {
            targets = new CommandTarget(args[0]).getMatched(player.getLocation());
        } else {
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            targets = targetPlayer == null ? new Entity[0] : new Entity[]{targetPlayer};
        }

        if (targets.length == 0) {
            sender.sendMessage(ChatColor.RED + "There's no entity matching the target.");
        } else {
            for (Entity target : targets) {
                String x = args[1], y = args[2], z = args[3];
                Location initial = player.getLocation();
                Location targetLocation = CommandUtils.getLocation(initial, x, y, z);
                if (args.length > 4) {
                    String yaw = args[4], pitch = args[5];
                    targetLocation = CommandUtils.getRotation(target.getLocation(), yaw, pitch);
                } else {
                    targetLocation.setYaw(target.getLocation().getYaw());
                    targetLocation.setPitch(target.getLocation().getPitch());
                }
                target.teleport(targetLocation);
                player.sendMessage("Teleported " + target.getName() + " to " + targetLocation.getX() + " " + targetLocation.getY() + " " + targetLocation.getZ());
            }
        }

        return true;
    }
}
