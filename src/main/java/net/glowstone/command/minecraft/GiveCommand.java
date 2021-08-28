package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.ItemIds;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

// FIXME: Ignores the 4th parameter, which is the item data tag
public class GiveCommand extends GlowVanillaCommand {

    public static final String PREFIX = NamespacedKey.MINECRAFT + ':';

    /**
     * Creates the instance for this command.
     */
    public GiveCommand() {
        super("give");
        setPermission("minecraft.command.give"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length < 2) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        String itemName = CommandUtils.toNamespaced(args[1]);
        Material type = ItemIds.getItem(itemName);
        if (type == null) {
            new LocalizedStringImpl("give.unknown", commandMessages.getResourceBundle())
                .sendInColor(ChatColor.RED, sender, itemName);
            return false;
        }
        ItemStack stack = new ItemStack(type);
        if (args.length >= 3) {
            String amountString = args[2];
            try {
                int amount = Integer.valueOf(amountString);
                if (amount > 64) {
                    new LocalizedStringImpl("give.too-many",
                        commandMessages.getResourceBundle())
                        .sendInColor(ChatColor.RED, sender, amount);
                    return false;
                } else if (amount < 1) {
                    new LocalizedStringImpl("give.too-few",
                        commandMessages.getResourceBundle())
                        .sendInColor(ChatColor.RED, sender, amount);
                    return false;
                }
                stack.setAmount(amount);
            } catch (NumberFormatException ex) {
                commandMessages.getGeneric(GenericMessage.NAN)
                    .sendInColor(ChatColor.RED, sender, amountString);
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
                    giveItem(sender, player, stack, commandMessages.getResourceBundle());
                }
            }
        } else {
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                commandMessages.getGeneric(GenericMessage.OFFLINE)
                    .sendInColor(ChatColor.RED, sender, name);
                return false;
            } else {
                giveItem(sender, player, stack, commandMessages.getResourceBundle());
            }
        }
        return true;
    }

    private void giveItem(CommandSender sender, Player player, ItemStack stack,
                          ResourceBundle resourceBundle) {
        player.getInventory().addItem(stack);
        new LocalizedStringImpl("give.done", resourceBundle)
            .send(sender, ItemIds.getName(stack.getType()), stack.getAmount(),
                player.getName());
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
