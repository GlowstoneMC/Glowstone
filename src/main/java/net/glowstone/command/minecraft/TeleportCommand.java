package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * /tp was an alias of this command until Minecraft 1.10, but now see {@link TpCommand}.
 */
public class TeleportCommand extends GlowVanillaCommand {

    private static final Entity[] NO_ENTITY = new Entity[0];

    /**
     * Creates the instance for this command.
     */
    public TeleportCommand() {
        super("teleport");
        setPermission("minecraft.command.teleport"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length < 4 || args.length == 5) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        if (!CommandUtils.isPhysical(sender)) {
            commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL)
                    .sendInColor(ChatColor.RED, sender);
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
            commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                    .sendInColor(ChatColor.RED, sender, args[0]);
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
                new LocalizedStringImpl("teleport.done", commandMessages.getResourceBundle())
                        .send(sender, target.getName(), targetLocation.getX(),
                                targetLocation.getY(), targetLocation.getZ());
            }
        }

        return true;
    }
}
