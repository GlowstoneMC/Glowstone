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

public class TeleportCommand extends VanillaCommand {

    private static final Entity[] NO_ENTITY = new Entity[0];

    public TeleportCommand() {
        super("teleport",
                I.tr("command.minecraft.teleport.description"), I.tr("command.minecraft.teleport.usage"),
                Collections.emptyList());
        setPermission("minecraft.command.teleport");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 4 || args.length == 5) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.teleport.usage")));
            return false;
        }

        if (!CommandUtils.isPhysical(sender)) {
            sender.sendMessage(I.tr(sender, "command.minecraft.teleport.physcial"));
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
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.teleport.nomatch"));
        } else {
            for (Entity target : targets) {
                String x = args[1], y = args[2], z = args[3];
                Location targetLocation = CommandUtils.getLocation(location, x, y, z);
                if (args.length > 4) {
                    String yaw = args[4], pitch = args[5];
                    targetLocation = CommandUtils.getRotation(target.getLocation(), yaw, pitch);
                } else {
                    targetLocation.setYaw(target.getLocation().getYaw());
                    targetLocation.setPitch(target.getLocation().getPitch());
                }
                target.teleport(targetLocation);
                sender.sendMessage(I.tr(sender, "command.minecraft.teleport.teleported", target.getName(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ()));
            }
        }

        return true;
    }
}
