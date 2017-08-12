package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class XpCommand extends VanillaCommand {
    public XpCommand() {
        super("xp", I.tr("command.minecraft.xp.description"), I.tr("command.minecraft.xp.usage"), Collections.emptyList());
        setPermission("minecraft.command.xp");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.xp.usage")));
            return false;
        } else {
            final String stringAmount = args[0], playerPattern = (args.length > 1) ? args[1] : null;
            final boolean addLevels = stringAmount.endsWith("l") || stringAmount.endsWith("L");
            int amount;
            List<Player> targets;

            // Handle the amount
            if (addLevels) {
                if (stringAmount.length() == 1) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.xp.amount") + " " + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.xp.usage")));
                    return false;
                }

                try {
                    amount = Integer.parseInt(stringAmount.substring(0, stringAmount.length() - 1));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(I.tr(sender, "command.generic.nan", stringAmount));
                    return false;
                }
            } else {
                try {
                    amount = Integer.parseInt(stringAmount);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(I.tr(sender, "command.generic.nan", stringAmount));
                    return false;
                }

                if (amount < 0) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.xp.negative"));
                    return false;
                }
            }

            // Handle the player(s)
            if (playerPattern != null && playerPattern.startsWith("@") && playerPattern.length() > 1 && CommandUtils.isPhysical(sender)) {
                final Location location = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
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
                    sender.sendMessage(I.tr(sender, "command.generic.player.missing", playerPattern));
                    return false;
                } else {
                    targets = Collections.singletonList(player);
                }
            }

            if (targets.isEmpty()) {
                sender.sendMessage(I.tr(sender, "command.generic.player.none"));
                return false;
            }

            // Give (or take) experience to the target(s)
            for (final Player player : targets) {
                if (addLevels) {
                    player.giveExpLevels(amount);

                    if (amount < 0) {
                        sender.sendMessage(I.tr(sender, "command.minecraft.xp.taken", (-amount), player.getName()));
                    } else {
                        sender.sendMessage(I.tr(sender, "command.minecraft.xp.given.lvl", amount, player.getName()));
                    }
                } else {
                    player.giveExp(amount);
                    sender.sendMessage(I.tr(sender, "command.minecraft.xp.given.xp", amount, player.getName()));
                }
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            return super.tabComplete(sender, alias, args);
        } else {
            return Collections.emptyList();
        }
    }
}
