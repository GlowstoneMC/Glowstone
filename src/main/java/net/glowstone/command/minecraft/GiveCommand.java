package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.constants.ItemIds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        super("give", "Gives an item to a player.", "/give <player> <item> [amount]", Collections.emptyList());
        setPermission("minecraft.command.give");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String name = args[0], itemName = args[1];
        Material type = ItemIds.getItem(itemName);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "There is no such item with name " + itemName);
            return false;
        }
        ItemStack stack = new ItemStack(type);
        if (args.length >= 3) {
            String amountString = args[2];
            try {
                int amount = Integer.valueOf(amountString);
                if (amount > 64) {
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + amount + ") is too big, it must be at most 64");
                    return false;
                } else if (amount < 1) {
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + amount + ") is too small, it must be at least 1");
                    return false;
                }
                stack.setAmount(amount);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + amountString + "' is not a valid number");
                return false;
            }
        }
        boolean targetsSupported = sender instanceof Entity || sender instanceof BlockCommandSender;
        if (name.startsWith("@") && name.length() >= 2 && targetsSupported) {
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
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not online.");
            } else {
                giveItem(sender, player, stack);
            }
        }
        return true;
    }

    private void giveItem(CommandSender sender, Player player, ItemStack stack) {
        player.getInventory().addItem(stack);
        sender.sendMessage("Given [" + ItemIds.getName(stack.getType()) + "] * " + stack.getAmount() + " to " + player.getName());
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
