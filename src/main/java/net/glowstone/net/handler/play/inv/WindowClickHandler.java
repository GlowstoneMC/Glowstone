package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.net.GlowSession;
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
        GlowServer.logger.info(session + ": " + message + " --> " + result);
        //session.send(new TransactionMessage(message.getId(), message.getTransaction(), result));
    }

    private boolean process(final GlowPlayer player, final WindowClickMessage message) {
        final InventoryView view = player.getOpenInventory();
        final ItemStack slotItem = view.getItem(message.getSlot());
        final ItemStack cursor = player.getItemOnCursor();

        final int slot = message.getSlot();

        // Determine inventory and slot clicked, used in some places
        // todo: whine and complain if users try to implement own inventory
        final GlowInventory inv;
        if (message.getSlot() < view.getTopInventory().getSize()) {
            inv = (GlowInventory) view.getTopInventory();
        } else {
            inv = (GlowInventory) view.getBottomInventory();
        }
        final int invSlot = view.convertSlot(message.getSlot());

        // check that the player has a correct view of the item
        if (!Objects.equals(message.getItem(), slotItem) && message.getMode() != 3) {
            // reject item change because of desynced inventory
            // in mode 3 (get), client does not send item under cursor
            player.sendItemChange(slot, slotItem);
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
                    if (cursor == null) {
                        if (slotItem != null) {
                            // pick up entire stack
                            view.setItem(slot, null);
                            player.setItemOnCursor(slotItem);
                            return true;
                        } else {
                            // nothing happens
                            return true;
                        }
                    } else if (inv.slotCanFit(invSlot, cursor)) {
                        // can only do anything if cursor could be placed in that slot
                        if (slotItem == null) {
                            // put down stack, up to inventory's max stack size
                            int transfer = Math.min(cursor.getAmount(), maxStack(inv, cursor.getType()) - cursor.getAmount());
                            if (transfer == cursor.getAmount()) {
                                // transfer whole stack
                                view.setItem(slot, cursor);
                                player.setItemOnCursor(null);
                                return true;
                            } else {
                                // partial transfer
                                ItemStack newStack = cursor.clone();
                                newStack.setAmount(transfer);
                                view.setItem(slot, newStack);
                                cursor.setAmount(cursor.getAmount() - transfer);
                                return true;
                            }
                        } else if (slotItem.isSimilar(cursor)) {
                            // items are stackable, transfer cursor -> slot
                            int transfer = Math.min(cursor.getAmount(), maxStack(inv, slotItem.getType()) - slotItem.getAmount());
                            if (transfer > 0) {
                                slotItem.setAmount(slotItem.getAmount() + transfer);
                                player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - transfer));
                                return true;
                            }
                        } else {
                            // swap cursor and slot
                            player.setItemOnCursor(slotItem);
                            inv.setItem(invSlot, cursor);
                            return true;
                        }
                    }
                } else {
                    // right click
                    if (cursor == null) {
                        if (slotItem == null) {
                            // do nothing
                            return false;
                        } else {
                            // pick up half (favor picking up)
                            int keepAmount = slotItem.getAmount() / 2;
                            ItemStack newCursor = slotItem.clone();
                            newCursor.setAmount(slotItem.getAmount() - keepAmount);

                            inv.setItem(invSlot, amountOrNull(slotItem, keepAmount));
                            player.setItemOnCursor(newCursor);
                            return true;
                        }
                    } else if (inv.slotCanFit(invSlot, cursor)) {
                        // can only do anything if cursor could be placed in that slot
                        if (slotItem == null) {
                            // place down 1 item
                            inv.setItem(invSlot, amountOrNull(cursor.clone(), 1));
                            player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - 1));
                            return true;
                        } else if (cursor.isSimilar(slotItem)) {
                            // place down 1 item if possible
                            if (slotItem.getAmount() + 1 <= maxStack(inv, slotItem.getType())) {
                                slotItem.setAmount(slotItem.getAmount() + 1);
                                player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - 1));
                                return true;
                            }
                        } else {
                            // swap non-similar stacks
                            player.setItemOnCursor(slotItem);
                            inv.setItem(invSlot, cursor);
                            return true;
                        }
                    }
                }
                return false;

            case 1: // shift click
                // item on cursor is totally ignored, no difference left/right click

                // todo
                if (GlowInventoryView.isDefault(view)) {
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

                // todo: agree with MC better

                if (slotItem == null) {
                    // nothing in current slot
                    if (destItem == null) {
                        // no action
                        return false;
                    } else {
                        // move from hotbar to current slot
                        inv.setItem(invSlot, destItem);
                        inv.setItem(hotbarSlot, null);
                        return true;
                    }
                } else {
                    if (destItem == null) {
                        // move from current slot to hotbar
                        inv.setItem(hotbarSlot, slotItem);
                        inv.setItem(invSlot, null);
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

    private ItemStack amountOrNull(ItemStack original, int amount) {
        original.setAmount(amount);
        return amount <= 0 ? null : original;
    }

    private int maxStack(Inventory inv, Material mat) {
        return Math.min(inv.getMaxStackSize(), mat.getMaxStackSize());
    }

}
