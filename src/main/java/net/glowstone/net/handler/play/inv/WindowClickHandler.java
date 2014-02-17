package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class WindowClickHandler implements MessageHandler<GlowSession, WindowClickMessage> {
    public void handle(GlowSession session, WindowClickMessage message) {
        boolean result = false;
        try {
            result = process(session.getPlayer(), message);
        } catch (IllegalArgumentException ex) {
            GlowServer.logger.warning(session + ": illegal argument while handling click: " + ex);
        }
        GlowServer.logger.info(session + " clicked: " + message + " --> " + result);
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), result));
    }

    private boolean process(final GlowPlayer player, final WindowClickMessage message) {
        InventoryView openView = player.getOpenInventory();

        final ItemStack slotItem = openView.getItem(message.getSlot());
        final ItemStack cursor = player.getItemOnCursor();

        // Determine inventory and slot clicked
        Inventory inv;
        if (message.getSlot() < openView.getTopInventory().getSize()) {
            inv = openView.getTopInventory();
        } else {
            inv = openView.getBottomInventory();
        }
        final int slot = openView.convertSlot(message.getSlot());

        if (slot < 0) {
            // todo: drop item
            player.setItemOnCursor(null);
            return true;
        }

        if (!Objects.equals(message.getItem(), slotItem) && message.getMode() != 3) {
            // reject item change because of desynced inventory
            // in mode 3 (get), client does not send item under cursor
            player.sendItemChange(message.getSlot(), slotItem);
            return false;
        }

        // m b s (* for -999)
        // 0 0   lmb
        //   1   rmb
        // 1 0   shift+lmb
        //   1   shift+rmb (same as 1/0)
        // 2 *   number key b+1
        // 3 2   middle click / duplicate (creative)
        // 4 0   drop
        // 4 1   ctrl + drop
        // 4 0 * lmb with no item (no-op)
        // 4 1 * rmb with no item (no-op)
        // 5 0 * start left drag
        // 5 1   add slot left drag
        // 5 2 * end left drag
        // 5 4 * start right drag
        // 5 5   add slot right drag
        // 5 6 * end right drag
        // 6 0   double click

        // todo: restrict sets to armor slots, crafting slots, so on
        // player.getInventory().getCraftingInventory().craft();

        switch (message.getMode()) {
            case 0: // normal click
                if (message.getButton() == 0) {
                    // left click
                    if (cursor == null && slotItem == null) {
                        // both are empty, do nothing
                        return false;
                    } else if (similar(slotItem, cursor) && cursor != null) {
                        // items are stackable, transfer cursor -> slot
                        int transfer = Math.min(cursor.getAmount(), maxStack(inv, slotItem.getType()) - slotItem.getAmount());
                        if (transfer > 0) {
                            slotItem.setAmount(slotItem.getAmount() + transfer);
                            player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - transfer));
                            return true;
                        }
                        return false;
                    } else {
                        // swap cursor and slot
                        player.setItemOnCursor(slotItem);
                        inv.setItem(slot, cursor);
                        return true;
                    }
                } else {
                    // right click
                    if (slotItem == null) {
                        // placing down
                        if (cursor == null) {
                            // do nothing
                            return false;
                        } else {
                            // place down 1 item
                            inv.setItem(slot, amountOrNull(cursor.clone(), 1));
                            player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - 1));
                            return true;
                        }
                    } else {
                        if (cursor == null) {
                            // pick up half (favor picking up)
                            int keepAmount = slotItem.getAmount() / 2;
                            ItemStack newCursor = slotItem.clone();
                            newCursor.setAmount(slotItem.getAmount() - keepAmount);

                            inv.setItem(slot, amountOrNull(slotItem, keepAmount));
                            player.setItemOnCursor(newCursor);
                            return true;
                        } else if (cursor.isSimilar(slotItem)) {
                            // place down 1 item if possible
                            if (slotItem.getAmount() + 1 <= maxStack(inv, slotItem.getType())) {
                                slotItem.setAmount(slotItem.getAmount() + 1);
                                player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - 1));
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            // swap non-similar stacks
                            player.setItemOnCursor(slotItem);
                            inv.setItem(slot, cursor);
                            return true;
                        }
                    }
                }
                // unreachable

            case 1: // shift click
                // item on cursor is totally ignored, no difference left/right click

                // todo
                if (GlowInventoryView.isDefault(openView)) {
                    // swap between main contents and hotbar

                } else {
                    // swap between two inventories

                }

                return false;

            case 2: // number key
                if (cursor != null) {
                    // only if no item on cursor
                    return false;
                }

                int hotbarSlot = message.getButton();
                ItemStack destItem = inv.getItem(hotbarSlot);

                // todo: verify bottom inventory is player's
                // todo: agree with MC better

                if (slotItem == null) {
                    // nothing in current slot
                    if (destItem == null) {
                        // no action
                        return false;
                    } else {
                        // move from hotbar to current slot
                        inv.setItem(slot, destItem);
                        inv.setItem(hotbarSlot, null);
                        return true;
                    }
                } else {
                    if (destItem == null) {
                        // move from current slot to hotbar
                        inv.setItem(hotbarSlot, slotItem);
                        inv.setItem(slot, null);
                        return true;
                    } else {
                        // both are non-null
                        // for now, just swap
                        // if slot is not in p
                        // todo
                        return false;
                    }
                }

            case 3: // get
                // only in creative and with no item on cursor
                if (cursor != null || player.getGameMode() != GameMode.CREATIVE) {
                    return false;
                }

                // copy and maximize item
                ItemStack stack = slotItem.clone();
                stack.setAmount(stack.getType().getMaxStackSize());
                player.setItemOnCursor(stack);
                return true;

            case 4: // drop
                return false;

            case 5: // drag
                return false;

            case 6: // double click
                return false;

            default: // unknown
                GlowServer.logger.info(player + " tried to use invalid click mode " + message.getMode());
                return false;
        }
    }

    private boolean similar(ItemStack a, ItemStack b) {
        return (a == b) || (a != null && a.isSimilar(b));
    }

    private ItemStack amountOrNull(ItemStack original, int amount) {
        original.setAmount(amount);
        return amount <= 0 ? null : original;
    }

    private int maxStack(Inventory inv, Material mat) {
        return Math.min(inv.getMaxStackSize(), mat.getMaxStackSize());
    }
}
