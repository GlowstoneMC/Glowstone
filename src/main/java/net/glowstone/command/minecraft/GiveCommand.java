package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
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

public class GiveCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public GiveCommand() {
        super("give", "Gives an item to a player.", "/give <player> <item> [amount]",
            Collections.emptyList());
        setPermission("minecraft.command.give");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String itemName = args[1];
        if (!itemName.startsWith("minecraft:")) {
            itemName = "minecraft:" + itemName;
        }
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
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + amount
                        + ") is too big, it must be at most 64");
                    return false;
                } else if (amount < 1) {
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + amount
                        + ") is too small, it must be at least 1");
                    return false;
                }
                stack.setAmount(amount);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + amountString + "' is not a valid number");
                return false;
            }
        }
        String name = args[0];
        if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
            Location location = sender instanceof Entity ? ((Entity) sender).getLocation()
                : ((BlockCommandSender) sender).getBlock().getLocation();
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
                sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not online.");
                return false;
            } else {
                giveItem(sender, player, stack);
            }
        }
        return true;
    }

    private void giveItem(CommandSender sender, Player player, ItemStack stack) {
        player.getInventory().addItem(stack);
        sender.sendMessage(
            "Given [" + ItemIds.getName(stack.getType()) + "] * " + stack.getAmount() + " to "
                + player.getName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        if (args.length == 2) {
            return ItemIds.getTabCompletion(args[1]);
        }
        return Collections.emptyList();
    }
}
