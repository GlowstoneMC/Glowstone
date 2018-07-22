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

public class TeleportCommand extends VanillaCommand {

    private static final Entity[] NO_ENTITY = new Entity[0];

    /**
     * Creates the instance for this command.
     */
    public TeleportCommand() {
        super("teleport",
            "Teleports entities to coordinates relative to the sender",
            "/teleport <target> <x> <y> <z> [<yaw> <pitch>]",
            Collections.emptyList());
        setPermission("minecraft.command.teleport");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length < 4 || args.length == 5) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        if (!CommandUtils.isPhysical(sender)) {
            sender.sendMessage("This command can only be executed by physical objects.");
            return false;
        }

        Location location = CommandUtils.getLocation(sender);
        Entity[] targets;
        if (args[0].startsWith("@")) {
            targets = new CommandTarget(sender, args[0]).getMatched(location);
        } else {
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            if (targetPlayer != null) {
                location = targetPlayer.getLocation();
            }
            targets = targetPlayer == null ? NO_ENTITY : new Entity[]{targetPlayer};
        }

        if (targets.length == 0) {
            sender.sendMessage(ChatColor.RED + "There's no entity matching the target.");
        } else {
            for (Entity target : targets) {
                String x = args[1];
                String y = args[2];
                String z = args[3];
                Location targetLocation = CommandUtils.getLocation(location, x, y, z);
                if (args.length > 4) {
                    String yaw = args[4];
                    String pitch = args[5];
                    targetLocation = CommandUtils.getRotation(target.getLocation(), yaw, pitch);
                } else {
                    targetLocation.setYaw(target.getLocation().getYaw());
                    targetLocation.setPitch(target.getLocation().getPitch());
                }
                target.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
                sender.sendMessage(
                    "Teleported " + target.getName() + " to " + targetLocation.getX() + " "
                        + targetLocation.getY() + " " + targetLocation.getZ());
            }
        }

        return true;
    }
}
