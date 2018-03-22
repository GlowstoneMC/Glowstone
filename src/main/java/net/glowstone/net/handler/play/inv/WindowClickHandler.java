package net.glowstone.net.handler.play.inv;

import static org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY;
import static org.bukkit.event.inventory.InventoryAction.NOTHING;

import com.flowpowered.network.MessageHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.DragTracker;
import net.glowstone.inventory.GlowCraftingInventory;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.inventory.WindowClickLogic;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.SetWindowSlotMessage;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public final class WindowClickHandler implements MessageHandler<GlowSession, WindowClickMessage> {

    @Override
    public void handle(GlowSession session, WindowClickMessage message) {
        boolean result = false;
        try {
            result = process(session.getPlayer(), message);
        } catch (IllegalArgumentException ex) {
            GlowServer.logger.warning(
                    session.getPlayer().getName() + ": illegal argument while handling click: "
                            + ex);
        }
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), result));
        if (!result) {
            GlowServer.logger.info(session.getPlayer().getName() + ": [rejected] " + message);
            session.getPlayer().updateInventory();
        }
    }

    private boolean process(GlowPlayer player, WindowClickMessage message) {
        int viewSlot = message.getSlot();
        InventoryView view = player.getOpenInventory();
        GlowInventory top = (GlowInventory) view.getTopInventory();
        GlowInventory bottom = (GlowInventory) view.getBottomInventory();
        ItemStack slotItem = InventoryUtil.itemOrEmpty(view.getItem(viewSlot));
        ItemStack cursor = player.getItemOnCursor();

        // check that the player has a correct view of the item
        if (!Objects.equals(message.getItem(), slotItem) && (message.getMode() == 0
                || message.getMode() == 1)) {
            // reject item change because of desynced inventory
            // in mode 3 (get) and 4 (drop), client does not send item in slot under cursor
            if (message.getMode() == 0 || !InventoryUtil.isEmpty(message.getItem())) {
                // in mode 1 (shift click), client does not send item in slot under cursor if the
                // action did not result in any change on the client side (inventory full) or
                // if there's an item under the cursor
                // recipe slot is not synced by design
                if (view.getTopInventory().getType() != InventoryType.CRAFTING || viewSlot >= view
                        .getTopInventory().getSize()
                        || ((GlowInventory) view.getTopInventory()).getSlot(viewSlot).getType()
                        != SlotType.RESULT) {
                    player.sendItemChange(viewSlot, slotItem);
                    return false;
                }
            }
        }

        // determine inventory and slot clicked, used in some places
        // todo: attempt to allow for users to implement their own inventory?
        // CraftBukkit does not allow this but it may be worth the trouble for
        // the extensibility.
        GlowInventory inv;
        if (viewSlot < top.getSize()) {
            inv = top;
        } else {
            inv = bottom;
        }
        int invSlot = view.convertSlot(viewSlot);

        if (invSlot == -1 || viewSlot == -1) {
            return true;
        }

        SlotType slotType = inv.getSlotType(invSlot);

        // handle dragging
        if (message.getMode() == 5) {
            // 5 0 * start left drag
            // 5 1   add slot left drag
            // 5 2 * end left drag
            // 5 4 * start right drag
            // 5 5   add slot right drag
            // 5 6 * end right drag

            DragTracker drag = player.getInventory().getDragTracker();
            boolean right = message.getButton() >= 4;

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
                    if (slots == null || InventoryUtil.isEmpty(cursor)) {
                        return false;
                    }

                    ItemStack newCursor = cursor.clone();
                    Map<Integer, ItemStack> newSlots = new HashMap<>();

                    int perSlot = right ? 1 : cursor.getAmount() / slots.size();
                    for (int dragSlot : slots) {
                        ItemStack oldItem = view.getItem(dragSlot);
                        if (InventoryUtil.isEmpty(oldItem) || cursor.isSimilar(oldItem)) {
                            Inventory dragInv = dragSlot < top.getSize() ? top : bottom;
                            int oldItemAmount = InventoryUtil.itemOrEmpty(oldItem).getAmount();
                            int transfer = Math.min(Math.min(perSlot, cursor.getAmount()),
                                    maxStack(dragInv, cursor.getType()) - oldItemAmount);
                            ItemStack newItem = combine(oldItem, cursor, transfer);
                            newSlots.put(dragSlot, newItem);
                            newCursor = amountOrEmpty(newCursor, newCursor.getAmount() - transfer);
                            if (InventoryUtil.isEmpty(newCursor)) {
                                break;
                            }
                        }
                    }

                    InventoryDragEvent event = new InventoryDragEvent(view, newCursor, cursor,
                            right, newSlots);
                    EventFactory.getInstance().callEvent(event);
                    if (event.isCancelled()) {
                        return false;
                    }

                    for (Entry<Integer, ItemStack> entry : newSlots.entrySet()) {
                        view.setItem(entry.getKey(), entry.getValue());
                    }
                    player.setItemOnCursor(newCursor);
                    return true;
                default:
                    return false;
            }
        }

        // determine what action will be taken and fire event
        ClickType clickType = WindowClickLogic
                .getClickType(message.getMode(), message.getButton(), viewSlot);
        InventoryAction action = WindowClickLogic.getAction(clickType, slotType, cursor, slotItem);

        if (clickType == ClickType.UNKNOWN || action == InventoryAction.UNKNOWN) {
            // show a warning for unknown click type
            GlowServer.logger.warning(
                    player.getName() + ": mystery window click " + clickType + "/" + action + ": "
                            + message);
        }

        // deny CLONE_STACK for non-creative mode players
        if (action == InventoryAction.CLONE_STACK && player.getGameMode() != GameMode.CREATIVE) {
            action = InventoryAction.NOTHING;
        }

        // determine whether NO_AI, HOTBAR_MOVE_AND_READD or HOTBAR_SWAP should be executed
        if (clickType == ClickType.NUMBER_KEY) {
            ItemStack destItem = bottom.getItem(message.getButton());
            if (InventoryUtil.isEmpty(slotItem)) {
                if (InventoryUtil.isEmpty(destItem) || !inv.itemPlaceAllowed(invSlot, destItem)) {
                    // both items are empty, do nothing
                    // or, current item is empty and destItem cannot be moved into current slot
                    action = InventoryAction.NOTHING;
                }
            } else if (inv != bottom || !inv.itemPlaceAllowed(invSlot, destItem)) {
                // target and source inventory are different or destItem cannot be placed in
                // current slot
                action = InventoryAction.HOTBAR_MOVE_AND_READD;
            }
        }

        if (WindowClickLogic.isPlaceAction(action)) {
            // check whether item can be dropped into the clicked slot
            if (!inv.itemPlaceAllowed(invSlot, cursor)) {
                // placement not allowed
                if (!InventoryUtil.isEmpty(slotItem) && slotItem.isSimilar(cursor)) {
                    // item in slot is the same as item on cursor
                    if (cursor.getAmount() + 1 == cursor.getMaxStackSize()) {
                        // There is still space under the cursor for one item
                        action = InventoryAction.PICKUP_ONE;
                    } else if (cursor.getAmount() < cursor.getMaxStackSize()) {
                        // There is still some space under the cursor
                        action = InventoryAction.PICKUP_SOME;
                    }
                } else {
                    action = InventoryAction.NOTHING;
                }
            }
        }

        InventoryClickEvent event = null;
        if (top == inv && top instanceof GlowCraftingInventory
                && top.getSlotType(invSlot) == SlotType.RESULT) {
            // Clicked on output slot of crafting inventory
            if (InventoryUtil.isEmpty(slotItem)) {
                // No crafting recipe result, don't do anything
                action = InventoryAction.NOTHING;
            }

            int cursorAmount = InventoryUtil.itemOrEmpty(cursor).getAmount();
            if (message.getMode() == 0 && slotItem.isSimilar(cursor)) {
                if (!InventoryUtil.isEmpty(slotItem)
                        && cursorAmount + slotItem.getAmount() <= slotItem.getMaxStackSize()) {
                    // if the player can take the whole result
                    if (WindowClickLogic.isPickupAction(action) || WindowClickLogic
                            .isPlaceAction(action)) {
                        // always take the whole crafting result out of the crafting inventories
                        action = InventoryAction.PICKUP_ALL;
                    } else if (action == InventoryAction.DROP_ONE_SLOT) {
                        // always drop the whole stack, not just single items
                        action = InventoryAction.DROP_ALL_SLOT;
                    }
                } else {
                    // if their cursor is full, do nothing
                    action = InventoryAction.NOTHING;
                }
            }
            // if we do anything, call the CraftItemEvent
            // this ignores whether the crafting process actually happens (full inventory, etc.)
            if (action != InventoryAction.NOTHING) {
                Recipe recipe = ((CraftingInventory) inv).getRecipe();
                if (clickType == ClickType.NUMBER_KEY) {
                    event = new CraftItemEvent(recipe, view, slotType, viewSlot, clickType, action,
                            message.getButton());
                } else {
                    event = new CraftItemEvent(recipe, view, slotType, viewSlot, clickType, action);
                }
            }
        }

        if (event == null) {
            if (clickType == ClickType.NUMBER_KEY) {
                event = new InventoryClickEvent(view, slotType, viewSlot, clickType, action,
                        message.getButton());
            } else {
                event = new InventoryClickEvent(view, slotType, viewSlot, clickType, action);
            }
        }

        EventFactory.getInstance().callEvent(event);
        if (event.isCancelled()) {
            int slot = event.getSlot();
            player.getSession().send(new SetWindowSlotMessage(player.getOpenWindowId(),
                    slot, event.getInventory().getItem(slot)));
            player.getSession().send(new SetWindowSlotMessage(-1, -1, player.getItemOnCursor()));
            return true;
        }

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
                view.setItem(viewSlot, InventoryUtil.createEmptyStack());
                int cursorAmount = InventoryUtil.itemOrEmpty(cursor).getAmount();
                player
                        .setItemOnCursor(amountOrEmpty(slotItem,
                                cursorAmount + slotItem.getAmount()));
                break;
            case PICKUP_HALF:
                // pick up half (favor picking up)
                int keepAmount = slotItem.getAmount() / 2;
                ItemStack newCursor = slotItem.clone();
                newCursor.setAmount(slotItem.getAmount() - keepAmount);

                inv.setItem(invSlot, amountOrEmpty(slotItem, keepAmount));
                player.setItemOnCursor(newCursor);
                break;
            case PICKUP_SOME:
                // pick up as many items as possible
                int pickUp = Math
                        .min(cursor.getMaxStackSize() - cursor.getAmount(), slotItem.getAmount());
                view.setItem(viewSlot, amountOrEmpty(slotItem, slotItem.getAmount() - pickUp));
                player.setItemOnCursor(amountOrEmpty(cursor, cursor.getAmount() + pickUp));
                break;
            case PICKUP_ONE:
                view.setItem(invSlot, amountOrEmpty(slotItem, slotItem.getAmount() - 1));
                player.setItemOnCursor(amountOrEmpty(cursor, cursor.getAmount() + 1));
                break;

            // PLACE_*
            case PLACE_ALL:
                view.setItem(viewSlot, combine(slotItem, cursor, cursor.getAmount()));
                player.setItemOnCursor(InventoryUtil.createEmptyStack());
                break;
            case PLACE_SOME: {
                // slotItem *should* never be empty in this situation?
                int transfer = Math.min(cursor.getAmount(),
                        maxStack(inv, slotItem.getType()) - slotItem.getAmount());
                view.setItem(viewSlot, combine(slotItem, cursor, transfer));
                player.setItemOnCursor(amountOrEmpty(cursor, cursor.getAmount() - transfer));
                break;
            }
            case PLACE_ONE:
                view.setItem(viewSlot, combine(slotItem, cursor, 1));
                player.setItemOnCursor(amountOrEmpty(cursor, cursor.getAmount() - 1));
                break;

            case SWAP_WITH_CURSOR:
                view.setItem(viewSlot, cursor);
                player.setItemOnCursor(slotItem);
                break;

            // DROP_*
            case DROP_ALL_CURSOR:
                if (!InventoryUtil.isEmpty(cursor)) {
                    drop(player, cursor);
                    player.setItemOnCursor(InventoryUtil.createEmptyStack());
                }
                break;
            case DROP_ONE_CURSOR:
                if (!InventoryUtil.isEmpty(cursor)) {
                    drop(player, amountOrEmpty(cursor.clone(), 1));
                    player.setItemOnCursor(amountOrEmpty(cursor, cursor.getAmount() - 1));
                }
                break;
            case DROP_ALL_SLOT:
                if (!InventoryUtil.isEmpty(slotItem)) {
                    drop(player, slotItem);
                    view.setItem(viewSlot, InventoryUtil.createEmptyStack());
                }
                break;
            case DROP_ONE_SLOT:
                if (InventoryUtil.isEmpty(slotItem)) {
                    drop(player, amountOrEmpty(slotItem.clone(), 1));
                    view.setItem(viewSlot, amountOrEmpty(slotItem, slotItem.getAmount() - 1));
                }
                break;

            // shift-click
            case MOVE_TO_OTHER_INVENTORY:
                if (!InventoryUtil.isEmpty(slotItem)) {
                    inv.handleShiftClick(player, view, viewSlot, slotItem);
                }
                break;

            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                GlowPlayerInventory playerInv = player.getInventory();
                int hotbarSlot = message.getButton();
                ItemStack destItem = playerInv.getItem(hotbarSlot);

                if (InventoryUtil.isEmpty(slotItem)) {
                    // nothing in current slot
                    if (InventoryUtil.isEmpty(destItem)) {
                        // no action
                        return false;
                    } else {
                        // move from hotbar to current slot
                        // do nothing if current slots does not accept the item
                        if (action == InventoryAction.HOTBAR_SWAP) {
                            inv.setItem(invSlot, destItem);
                            playerInv.setItem(hotbarSlot, InventoryUtil.createEmptyStack());
                        }
                        return true;
                    }
                } else {
                    if (InventoryUtil.isEmpty(destItem)) {
                        // move from current slot to hotbar
                        playerInv.setItem(hotbarSlot, slotItem);
                        inv.setItem(invSlot, InventoryUtil.createEmptyStack());
                        return true;
                    } else {
                        // both are non-empty, swap them
                        playerInv.setItem(hotbarSlot, slotItem);
                        if (action == InventoryAction.HOTBAR_SWAP) {
                            inv.setItem(invSlot, destItem);
                        } else {
                            inv.setItem(invSlot, InventoryUtil.createEmptyStack());
                            playerInv.addItem(destItem);
                        }
                        return true;
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
                if (InventoryUtil.isEmpty(cursor)) {
                    return false;
                }

                int slotCount = view.countSlots();
                for (int i = 0;
                     i < slotCount && cursor.getAmount() < maxStack(inv, cursor.getType()); ++i) {
                    ItemStack item = view.getItem(i);
                    SlotType type = (i < top.getSize() ? top : bottom)
                            .getSlotType(view.convertSlot(i));
                    if (InventoryUtil.isEmpty(item) || !cursor.isSimilar(item)
                            || type == SlotType.RESULT) {
                        continue;
                    }
                    int transfer = Math.min(item.getAmount(),
                            maxStack(inv, cursor.getType()) - cursor.getAmount());
                    cursor.setAmount(cursor.getAmount() + transfer);
                    view.setItem(i, amountOrEmpty(item, item.getAmount() - transfer));
                }
                break;
            default:
                handled = false;
        }

        if (handled && top == inv && top instanceof GlowCraftingInventory
                && top.getSlotType(invSlot) == SlotType.RESULT && action != MOVE_TO_OTHER_INVENTORY
                && action != NOTHING) {
            // If we are crafting (but not using shift click because no more items can be crafted
            // for the given pattern. If a new item can be crafted with another pattern, a new
            // click is required).
            final GlowCraftingInventory glowCraftingInventory = (GlowCraftingInventory) top;
            glowCraftingInventory.craft();
            // Notify the player the result slot changed
            player.sendItemChange(viewSlot, glowCraftingInventory.getResult());
        }

        if (!handled) {
            GlowServer.logger.warning(
                    player.getName() + ": unhandled click action " + action + " for " + message);
        }

        return handled;
    }

    private void drop(GlowPlayer player, ItemStack stack) {
        // drop the stack if it's valid
        if (!InventoryUtil.isEmpty(stack)) {
            player.drop(stack);
        }
    }

    private ItemStack combine(ItemStack slotItem, ItemStack cursor, int amount) {
        if (InventoryUtil.isEmpty(slotItem)) {
            ItemStack stack = cursor.clone();
            stack.setAmount(amount);
            return stack;
        } else if (slotItem.isSimilar(cursor)) {
            slotItem.setAmount(slotItem.getAmount() + amount);
            return slotItem;
        } else {
            throw new IllegalArgumentException(
                    "Trying to combine dissimilar " + slotItem + " and " + cursor);
        }
    }

    private ItemStack amountOrEmpty(ItemStack original, int amount) {
        original.setAmount(amount);
        return amount <= 0 ? InventoryUtil.createEmptyStack() : original;
    }

    private int maxStack(Inventory inv, Material mat) {
        return Math.min(inv.getMaxStackSize(), mat.getMaxStackSize());
    }
}
