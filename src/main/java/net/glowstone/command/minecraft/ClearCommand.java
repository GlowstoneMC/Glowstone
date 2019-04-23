package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowVanillaCommand;
import net.glowstone.constants.ItemIds;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClearCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public ClearCommand() {
        super("clear");
        setPermission("minecraft.command.clear"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        final ResourceBundle resourceBundle = messages.getResourceBundle();
        if (args.length == 0) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                return clearAll(sender, player, null, -1, -1, resourceBundle);
            } else {
                sendUsageMessage(sender, messages);
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
                messages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            } else {
                players.add(player);
            }
        }
        if (players.size() == 0) {
            messages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                    .sendInColor(ChatColor.RED, sender, name);
            return false;
        }
        if (args.length >= 2) {
            String itemName = CommandUtils.toNamespaced(args[1]);
            Material type = ItemIds.getItem(itemName);
            if (type == null) {
                new LocalizedStringImpl("clear.no-such-item", resourceBundle)
                        .sendInColor(ChatColor.RED, sender, itemName);
                return false;
            }
            if (args.length >= 3) {
                String dataString = args[2];
                int data;
                try {
                    data = Integer.valueOf(dataString);
                } catch (NumberFormatException ex) {
                    messages.getGeneric(GenericMessage.NAN)
                            .sendInColor(ChatColor.RED, sender, dataString);
                    return false;
                }
                if (data < -1) {
                    new LocalizedStringImpl("clear.negative", resourceBundle)
                            .sendInColor(ChatColor.RED, sender, data);
                    return false;
                }
                if (args.length >= 4) {
                    String amountString = args[3];
                    int amount;
                    try {
                        amount = Integer.valueOf(amountString);
                    } catch (NumberFormatException ex) {
                        messages.getGeneric(GenericMessage.NAN)
                                .sendInColor(ChatColor.RED, sender, amountString);
                        return false;
                    }
                    if (amount < -1) {
                        new LocalizedStringImpl("clear.negative", resourceBundle)
                                .sendInColor(ChatColor.RED, sender, amount);
                        return false;
                    }
                    if (args.length >= 5) {
                        new LocalizedStringImpl("clear.tag-unsupported", resourceBundle)
                                .sendInColor(ChatColor.RED, sender);
                        return false;
                    } else {
                        boolean success = true;
                        for (Player player : players) {
                            if (!clearAll(sender, player, type, data, amount, resourceBundle)) {
                                success = false;
                            }
                        }
                        return success;
                    }
                } else {
                    boolean success = true;
                    for (Player player : players) {
                        if (!clearAll(sender, player, type, data, -1, resourceBundle)) {
                            success = false;
                        }
                    }
                    return success;
                }
            } else {
                boolean success = true;
                for (Player player : players) {
                    if (!clearAll(sender, player, type, -1, -1, resourceBundle)) {
                        success = false;
                    }
                }
                return success;
            }
        } else {
            boolean success = true;
            for (Player player : players) {
                if (!clearAll(sender, player, null, -1, -1, resourceBundle)) {
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
            int maxCount, ResourceBundle resourceBundle) {
        int count = countAllItems(player.getInventory(), material, data, maxCount);
        if (maxCount == 0) {
            new LocalizedStringImpl("clear.count", resourceBundle).send(
                    sender, player.getName(), count);
            return true;
        }
        if (count == 0) {
            new LocalizedStringImpl("clear.empty", resourceBundle).send(
                    sender, player.getName());
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
        if (count == 1) {
            new LocalizedStringImpl("clear.done.singular", resourceBundle).send(
                    sender, player.getName());
        } else {
            new LocalizedStringImpl("clear.done", resourceBundle).send(
                    sender, player.getName(), count);
        }
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
