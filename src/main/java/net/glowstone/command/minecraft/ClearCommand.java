package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.ItemIds;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClearCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public ClearCommand() {
        super("clear", "Clears the content of a player's inventory.",
            "/clear [player] [item] [data] [maxCount]", Collections.emptyList());
        setPermission("minecraft.command.clear");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                return clearAll(sender, player, null, -1, -1);
            } else {
                sender
                    .sendMessage(ChatColor.RED + "Usage: /clear <player> [item] [data] [maxCount]");
                return false;
            }
        }
        String name = args[0];
        List<Player> players = new ArrayList<>();
        if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
            Location location = sender instanceof Entity ? ((Entity) sender).getLocation()
                : ((BlockCommandSender) sender).getBlock().getLocation();
            CommandTarget target = new CommandTarget(sender, name);
            Entity[] matched = target.getMatched(location);
            for (Entity entity : matched) {
                if (entity instanceof Player) {
                    players.add((Player) entity);
                }
            }
        } else {
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + name + "' cannot be found");
                return false;
            } else {
                players.add(player);
            }
        }
        if (players.size() == 0) {
            sender.sendMessage(ChatColor.RED + "Player '" + name + "' cannot be found");
            return false;
        }
        if (args.length >= 2) {
            String itemName = args[1];
            if (!itemName.startsWith("minecraft:")) {
                itemName = "minecraft:" + itemName;
            }
            Material type = ItemIds.getItem(itemName);
            if (type == null) {
                sender.sendMessage(ChatColor.RED + "There is no such item with name " + itemName);
                return false;
            }
            if (args.length >= 3) {
                String dataString = args[2];
                int data;
                try {
                    data = Integer.valueOf(dataString);
                } catch (NumberFormatException ex) {
                    sender
                        .sendMessage(ChatColor.RED + "'" + dataString + "' is not a valid number");
                    return false;
                }
                if (data < -1) {
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + data
                        + ") is too small, it must be at least -1");
                    return false;
                }
                if (args.length >= 4) {
                    String amountString = args[3];
                    int amount;
                    try {
                        amount = Integer.valueOf(amountString);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(
                            ChatColor.RED + "'" + amountString + "' is not a valid number");
                        return false;
                    }
                    if (amount < -1) {
                        sender.sendMessage(ChatColor.RED + "The number you have entered (" + amount
                            + ") is too small, it must be at least -1");
                        return false;
                    }
                    if (args.length >= 5) {
                        sender.sendMessage(
                            ChatColor.RED + "Sorry, item data-tags are not supported yet.");
                        return false;
                    } else {
                        boolean success = true;
                        for (Player player : players) {
                            if (!clearAll(sender, player, type, data, amount)) {
                                success = false;
                            }
                        }
                        return success;
                    }
                } else {
                    boolean success = true;
                    for (Player player : players) {
                        if (!clearAll(sender, player, type, data, -1)) {
                            success = false;
                        }
                    }
                    return success;
                }
            } else {
                boolean success = true;
                for (Player player : players) {
                    if (!clearAll(sender, player, type, -1, -1)) {
                        success = false;
                    }
                }
                return success;
            }
        } else {
            boolean success = true;
            for (Player player : players) {
                if (!clearAll(sender, player, null, -1, -1)) {
                    success = false;
                }
            }
            return success;
        }
    }

    private int countAllItems(Inventory inventory, Material material, int data, int maxCount) {
        if (material == null) {
            return Arrays.stream(inventory.getContents())
                .filter(stack -> !InventoryUtil.isEmpty(stack)).mapToInt(ItemStack::getAmount)
                .sum();
        }
        int count = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack.getType() == material && (data == -1 || data == stack.getData().getData())
                && (maxCount == -1 || maxCount == 0 || count < maxCount)) {
                if (maxCount == -1 || maxCount == 0) {
                    count += stack.getAmount();
                } else {
                    for (int i = 0; i < stack.getAmount(); i++) {
                        if (count < maxCount) {
                            count++;
                        } else {
                            return count;
                        }
                    }
                }
            }
        }
        return count;
    }

    private boolean clearAll(CommandSender sender, Player player, Material material, int data,
        int maxCount) {
        int count = countAllItems(player.getInventory(), material, data, maxCount);
        if (maxCount == 0) {
            sender.sendMessage(player.getName() + " has " + count
                    + " items that match the criteria");
            return true;
        }
        if (count == 0) {
            sender.sendMessage(
                ChatColor.RED + "Could not clear the inventory of " + player.getName()
                    + ", no items to remove");
            return false;
        }
        if (material == null) {
            player.getInventory().clear();
        } else {
            int remaining = maxCount;
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack.getType() == material && (data == -1 || data == stack.getData()
                    .getData())) {
                    // matches type and data
                    if (maxCount == -1) {
                        player.getInventory().remove(stack);
                    } else {
                        int oldAmount = stack.getAmount();
                        int removed = Math.min(oldAmount, remaining);
                        stack.setAmount(oldAmount - removed);
                        remaining -= removed;
                    }
                }
                if (remaining == 0) {
                    break;
                }
            }
        }
        sender.sendMessage(
            "Cleared the inventory of " + player.getName() + ", removing " + count + " items");
        return true;
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
