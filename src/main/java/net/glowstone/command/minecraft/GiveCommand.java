package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.ItemIds;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveCommand extends VanillaCommand {
    public GiveCommand() {
        super("give", I.tr("command.minecraft.give.description"), I.tr("command.minecraft.give.usage"), Collections.emptyList());
        setPermission("minecraft.command.give");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length < 2) {
            sender.sendMessage(I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.give.usage")));
            return false;
        }
        String name = args[0], itemName = args[1];
        if (!itemName.startsWith("minecraft:")) {
            itemName = "minecraft:" + itemName;
        }
        Material type = ItemIds.getItem(itemName);
        if (type == null) {
            sender.sendMessage(I.tr(sender, "command.minecraft.give.missing", itemName));
            return false;
        }
        ItemStack stack = new ItemStack(type);
        if (args.length >= 3) {
            String amountString = args[2];
            try {
                int amount = Integer.valueOf(amountString);
                if (amount > 64) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.give.toobig", amount));
                    return false;
                } else if (amount < 1) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.give.toosmall", amount));
                    return false;
                }
                stack.setAmount(amount);
            } catch (NumberFormatException ex) {
                sender.sendMessage(I.tr(sender, "command.generic.nan", amountString));
                return false;
            }
        }
        if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
            Location location = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
            CommandTarget target = new CommandTarget(sender, name);
            Entity[] matched = target.getMatched(location);
            for (Entity entity : matched) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    giveItem(sender, player, stack);
                }
            }
        } else {
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                sender.sendMessage(I.tr(sender, "command.generic.player.offline", name));
                return false;
            } else {
                giveItem(sender, player, stack);
            }
        }
        return true;
    }

    private void giveItem(CommandSender sender, Player player, ItemStack stack) {
        player.getInventory().addItem(stack);
        sender.sendMessage(I.tr(sender, "command.minecraft.give.given", ItemIds.getName(stack.getType()), stack.getAmount(), player.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        if (args.length == 2) {
            String start = args[1];
            if (!"minecraft:".startsWith(start)) {
                int colon = start.indexOf(':');
                start = "minecraft:" + start.substring(colon == -1 ? 0 : (colon + 1));
            }
            return (List) StringUtil.copyPartialMatches(start, ItemIds.getIds(), new ArrayList(ItemIds.getIds().size()));
        }
        return Collections.emptyList();
    }
}
