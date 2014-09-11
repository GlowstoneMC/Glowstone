package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.DragTracker;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.inventory.WindowClickLogic;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WindowClickHandler implements MessageHandler<GlowSession, WindowClickMessage> {
    @Override
    public void handle(GlowSession session, WindowClickMessage message) {
        boolean result = false;
        try {
            result = process(session.getPlayer(), message);
        } catch (IllegalArgumentException ex) {
            GlowServer.logger.warning(session.getPlayer().getName() + ": illegal argument while handling click: " + ex);
        }
        if (!result) {
            GlowServer.logger.info(session.getPlayer().getName() + ": [rejected] " + message);
        }
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), result));
    }

    private boolean process(final GlowPlayer player, final WindowClickMessage message) {
        final int viewSlot = message.getSlot();
        final InventoryView view = player.getOpenInventory();
        final ItemStack slotItem = view.getItem(viewSlot);
        final ItemStack cursor = player.getItemOnCursor();

        // check that the player has a correct view of the item
        if (!Objects.equals(message.getItem(), slotItem) && (message.getMode() == 0 || message.getMode() == 1)) {
            // reject item change because of desynced inventory
            // in mode 3 (get) and 4 (drop), client does not send item under cursor
            player.sendItemChange(viewSlot, slotItem);
            return false;
        }

        // determine inventory and slot clicked, used in some places
        // todo: attempt to allow for users to implement their own inventory?
        // CraftBukkit does not allow this but it may be worth the trouble for
        // the extensibility.
        final GlowInventory inv;
        if (viewSlot < view.getTopInventory().getSize()) {
            inv = (GlowInventory) view.getTopInventory();
        } else {
            inv = (GlowInventory) view.getBottomInventory();
        }
        final int invSlot = view.convertSlot(viewSlot);
        final InventoryType.SlotType slotType = inv.getSlotType(invSlot);

        // handle dragging
        if (message.getMode() == 5) {
            // 5 0 * start left drag
            // 5 1   add slot left drag
            // 5 2 * end left drag
            // 5 4 * start right drag
            // 5 5   add slot right drag
            // 5 6 * end right drag

            DragTracker drag = player.getInventory().getDragTracker();
            boolean right = (message.getButton() >= 4);

            switch (message.getButton()) {
                case 0: // start left drag
                case 4: // start right drag
                    return drag.start(right);

                case 1: // add slot left
                case 5: // add slot right
                    return drag.addSlot(right, message.getSlot());

                case 2: // end left drag
                case 6: // end right drag
                    List<Integer> slots = drag.finish(right);
                    if (slots == null || cursor == null) {
                        return false;
                    }

                    ItemStack newCursor = cursor.clone();
                    Map<Integer, ItemStack> newSlots = new HashMap<>();

                    int perSlot = right ? 1 : cursor.getAmount() / slots.size();
                    for (int dragSlot : slots) {
                        ItemStack oldItem = view.getItem(dragSlot);
                        if (oldItem == null || cursor.isSimilar(oldItem)) {
                            ItemStack newItem = combine(oldItem, cursor, perSlot);
                            newSlots.put(dragSlot, newItem);
                            newCursor = amountOrNull(newCursor, newCursor.getAmount() - perSlot);
                            if (newCursor == null) {
                                break;
                            }
                        }
                    }

                    InventoryDragEvent event = new InventoryDragEvent(view, newCursor, cursor, right, newSlots);
                    if (event.isCancelled()) {
                        return false;
                    }

                    for (Map.Entry<Integer, ItemStack> entry : newSlots.entrySet()) {
                        view.setItem(entry.getKey(), entry.getValue());
                    }
                    player.setItemOnCursor(newCursor);
                    return true;
            }

            return false;
        }

        // determine what action will be taken and fire event
        final ClickType clickType = WindowClickLogic.getClickType(message.getMode(), message.getButton(), viewSlot);
        InventoryAction action = WindowClickLogic.getAction(clickType, slotType, cursor, slotItem);

        if (clickType == ClickType.UNKNOWN || action == InventoryAction.UNKNOWN) {
            // show a warning for unknown click type
            GlowServer.logger.warning(player.getName() + ": mystery window click " + clickType + "/" + action + ": " + message);
        }

        // deny CLONE_STACK for non-creative mode players
        if (action == InventoryAction.CLONE_STACK && player.getGameMode() != GameMode.CREATIVE) {
            action = InventoryAction.NOTHING;
        }

        final InventoryClickEvent event;
        if (clickType == ClickType.NUMBER_KEY) {
            event = new InventoryClickEvent(view, slotType, viewSlot, clickType, action, message.getButton());
        } else {
            event = new InventoryClickEvent(view, slotType, viewSlot, clickType, action, message.getButton());
        }

        EventFactory.callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        // todo: restrict sets to certain slots and do crafting as needed

        boolean handled = true;
        switch (action) {
            case NOTHING:
                break;

            case UNKNOWN:
                // return false rather than break - this case is "handled" but
                // any action the client tried to take should be denied
                return false;

            // PICKUP_*
            case PICKUP_ALL:
                view.setItem(viewSlot, null);
                player.setItemOnCursor(slotItem);
                break;
            case PICKUP_HALF: {
                // pick up half (favor picking up)
                int keepAmount = slotItem.getAmount() / 2;
                ItemStack newCursor = slotItem.clone();
                newCursor.setAmount(slotItem.getAmount() - keepAmount);

                inv.setItem(invSlot, amountOrNull(slotItem, keepAmount));
                player.setItemOnCursor(newCursor);
                break;
            }
            case PICKUP_SOME:
            case PICKUP_ONE:
                // nb: only happens when the item on the cursor cannot go in
                // the slot, so the item is tried to be picked up instead
                handled = false;
                break;

            // PLACE_*
            case PLACE_ALL:
                view.setItem(viewSlot, combine(slotItem, cursor, cursor.getAmount()));
                player.setItemOnCursor(null);
                break;
            case PLACE_SOME: {
                // slotItem *should* never be null in this situation?
                int transfer = Math.min(cursor.getAmount(), maxStack(inv, slotItem.getType()) - slotItem.getAmount());
                view.setItem(viewSlot, combine(slotItem, cursor, transfer));
                player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - transfer));
                break;
            }
            case PLACE_ONE:
                view.setItem(viewSlot, combine(slotItem, cursor, 1));
                player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - 1));
                break;

            case SWAP_WITH_CURSOR:
                view.setItem(viewSlot, cursor);
                player.setItemOnCursor(slotItem);
                break;

            // DROP_*
            case DROP_ALL_CURSOR:
                if (cursor != null) {
                    drop(player, cursor);
                    player.setItemOnCursor(null);
                }
                break;
            case DROP_ONE_CURSOR:
                if (cursor != null) {
                    drop(player, amountOrNull(cursor.clone(), 1));
                    player.setItemOnCursor(amountOrNull(cursor, cursor.getAmount() - 1));
                }
                break;
            case DROP_ALL_SLOT:
                if (slotItem != null) {
                    drop(player, slotItem);
                    view.setItem(viewSlot, null);
                }
                break;
            case DROP_ONE_SLOT:
                if (slotItem != null) {
                    drop(player, amountOrNull(slotItem.clone(), 1));
                    view.setItem(viewSlot, amountOrNull(slotItem, slotItem.getAmount() - 1));
                }
                break;

            // shift-click
            case MOVE_TO_OTHER_INVENTORY:
                /*
                 * armor slots are considered as top inventory if in crafting mode
                 *
                 * if in bottom inventory:
                 *   try to place in top inventory
                 *   if default view: try to place in armor slots
                 *   try ???
                 * if in top inventory:
                 *   try to place in main slots of bottom inventory (9..36)
                 *   try to place in hotbar slots of bottom inventory (0..9)
                 */
                // todo: shift-click logic is a disaster
                if (GlowInventoryView.isDefault(view)) {
                    // if in main contents, try to flip to hotbar, starting at left
                    if (slotType == InventoryType.SlotType.CONTAINER) {
                        ItemStack stack = slotItem.clone();
                        // first try to flip to armor
                        stack = shiftClick(stack, player.getInventory(), 36, 40);
                        // otherwise try to flip to hotbar starting at left
                        stack = shiftClick(stack, player.getInventory(), 0, 9);
                        // update the source
                        inv.setItem(invSlot, stack);
                    } else {
                        // otherwise, try to flip to main contents
                        inv.setItem(invSlot, shiftClick(slotItem, player.getInventory(), 9, 36));
                    }
                } else {
                    // swap between two inventories
                    handled = false;
                }
                break;

            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                // nb: difference between swap and move/readd is whether the
                // item in the hotbar is allowed to be placed in the slot.
                // this difference is currently unhandled.

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

            case CLONE_STACK:
                // only in creative and with no item on cursor handled earlier
                // copy and maximize item
                ItemStack stack = slotItem.clone();
                stack.setAmount(stack.getType().getMaxStackSize());
                player.setItemOnCursor(stack);
                break;

            case COLLECT_TO_CURSOR:
                if (cursor == null) {
                    return false;
                }

                // todo: double-check if this is the correct order to check slots in
                for (int i = 0; i < view.countSlots() && cursor.getAmount() < maxStack(inv, cursor.getType()); ++i) {
                    ItemStack item = view.getItem(i);
                    if (item == null || !cursor.isSimilar(item)) {
                        continue;
                    }

                    int transfer = Math.min(item.getAmount(), maxStack(inv, cursor.getType()) - cursor.getAmount());
                    cursor.setAmount(cursor.getAmount() + transfer);
                    view.setItem(i, amountOrNull(item, item.getAmount() - transfer));
                }
                break;
        }

        if (!handled) {
            GlowServer.logger.warning(player.getName() + ": unhandled click action " + action + " for " + message);
        }

        return handled;
    }

    private void drop(GlowPlayer player, ItemStack stack) {
        if (stack == null || stack.getAmount() <= 0) {
            return;
        }
        // drop item with the given contents and throw it the way the player is facing
        // this 0.2 number has been pulled out of thin air
        Vector vel = player.getLocation().getDirection().multiply(0.2);
        player.getWorld().dropItem(player.getEyeLocation(), stack).setVelocity(vel);
    }

    private ItemStack combine(ItemStack slotItem, ItemStack cursor, int amount) {
        if (slotItem == null) {
            ItemStack stack = cursor.clone();
            stack.setAmount(amount);
            return stack;
        } else if (slotItem.isSimilar(cursor)) {
            slotItem.setAmount(slotItem.getAmount() + amount);
            return slotItem;
        } else {
            throw new IllegalArgumentException("Trying to combine dissimilar " + slotItem + " and " + cursor);
        }
    }

    private ItemStack shiftClick(ItemStack stack, GlowInventory target, int start, int end) {
        if (stack == null) return null;

        int delta = (end < start) ? -1 : 1;

        // shift-click logic is horrifying
        for (int i = start; i != end && stack != null; i += delta) {
            if (target.itemShiftClickAllowed(i, stack)) {
                ItemStack slot = target.getItem(i);
                if (slot == null) {
                    slot = stack.clone();
                    slot.setAmount(0);
                }
                if (slot.isSimilar(stack)) {
                    int amount = slot.getAmount();
                    int transfer = Math.min(stack.getAmount(), maxStack(target, stack.getType()) - amount);
                    if (transfer > 0) {
                        slot.setAmount(amount + transfer);
                        stack = amountOrNull(stack, stack.getAmount() - transfer);
                        target.setItem(i, slot);
                    }
                }
            }
        }

        return stack;
    }

    private ItemStack amountOrNull(ItemStack original, int amount) {
        original.setAmount(amount);
        return amount <= 0 ? null : original;
    }

    private int maxStack(Inventory inv, Material mat) {
        return Math.min(inv.getMaxStackSize(), mat.getMaxStackSize());
    }

}
