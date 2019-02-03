package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class XpCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public XpCommand() {
        super("xp");
        setPermission("minecraft.command.xp"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (args.length == 0) {
            sendUsageMessage(sender, commandMessages);
            return false;
        } else {
            final String stringAmount = args[0];
            final String playerPattern = (args.length > 1) ? args[1] : null;
            final boolean addLevels = stringAmount.endsWith("l") || stringAmount.endsWith("L");
            int amount;
            List<Player> targets;

            // Handle the amount
            if (addLevels) {
                if (stringAmount.length() == 1) {
                    sender.sendMessage(
                        ChatColor.RED + "Please specify an amount. Usage: " + usageMessage);
                    return false;
                }

                try {
                    amount = Integer.parseInt(stringAmount.substring(0, stringAmount.length() - 1));
                } catch (NumberFormatException ex) {
                    commandMessages.getNotANumber().send(sender, stringAmount);
                    return false;
                }
            } else {
                try {
                    amount = Integer.parseInt(stringAmount);
                } catch (NumberFormatException ex) {
                    commandMessages.getNotANumber().send(sender, stringAmount);
                    return false;
                }

                if (amount < 0) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot give player negative experience points.");
                    return false;
                }
            }

            // Handle the player(s)
            if (playerPattern != null && playerPattern.startsWith("@") && playerPattern.length() > 1
                && CommandUtils.isPhysical(sender)) {
                final Location location = sender instanceof Entity ? ((Entity) sender).getLocation()
                    : ((BlockCommandSender) sender).getBlock().getLocation();
                final Entity[] entities = new CommandTarget(sender, args[1]).getMatched(location);
                targets = new ArrayList<>();

                for (final Entity entity : entities) {
                    if (entity instanceof Player) {
                        targets.add((Player) entity);
                    }
                }
            } else {
                Player player;

                if (playerPattern == null) { // If no players, get the current one
                    player = sender instanceof Player ? (Player) sender : null;
                } else {
                    player = Bukkit.getPlayerExact(playerPattern);
                }

                if (player == null) {
                    sender.sendMessage(
                        ChatColor.RED + "Player " + playerPattern + " cannot be found");
                    return false;
                } else {
                    targets = Collections.singletonList(player);
                }
            }

            if (targets.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No players found.");
                return false;
            }

            // Give (or take) experience to the target(s)
            for (final Player player : targets) {
                if (addLevels) {
                    player.giveExpLevels(amount);

                    if (amount < 0) {
                        sender.sendMessage("Taken " + (-amount) + " levels to " + player.getName());
                    } else {
                        sender.sendMessage("Given " + amount + " levels to " + player.getName());
                    }
                } else {
                    player.giveExp(amount);
                    sender.sendMessage("Given " + amount + " experience to " + player.getName());
                }
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 2) {
            return super.tabComplete(sender, alias, args);
        } else {
            return Collections.emptyList();
        }
    }
}
