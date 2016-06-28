package net.glowstone.command;

import net.glowstone.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
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
        // TODO: Change this to command targets (@a, @e, @p, etc.)
        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null || !player.canSee(target)) {
            sender.sendMessage("There's no player by that name online.");
        } else {
            String x = args[1], y = args[2], z = args[3];
            Location initial = player.getLocation();
            Location targetLocation = CommandUtils.getLocation(initial, x, y, z);
            if (args.length > 4) {
                String yaw = args[4], pitch = args[5];
                targetLocation = CommandUtils.getRotation(target.getLocation(), yaw, pitch);
            }
            target.teleport(targetLocation);
            player.sendMessage("Teleported " + sender.getName() + " to " + targetLocation.getX() + " " + targetLocation.getY() + " " + targetLocation.getZ());
        }

        return true;
    }
}
