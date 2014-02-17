package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowCraftingInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class WindowClickHandler implements MessageHandler<GlowSession, WindowClickMessage> {
    public void handle(GlowSession session, WindowClickMessage message) {
        boolean result = process(session.getPlayer(), message);
        //GlowServer.logger.info(session + " clicked: " + message + " --> " + result);
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), result));
    }

    private boolean process(final GlowPlayer player, final WindowClickMessage message) {
        // Determine inventory and slot clicked
        InventoryView openView = player.getOpenInventory();
        Inventory inv;
        int slot = message.getSlot();
        if (slot < openView.getTopInventory().getSize()) {
            inv = openView.getTopInventory();
        } else {
            inv = openView.getBottomInventory();
        }
        slot = openView.convertSlot(slot);

        if (slot < 0) {
            // todo: drop item
            player.setItemOnCursor(null);
            return true;
        }

        ItemStack currentItem = inv.getItem(slot);

        /*if (player.getGameMode() == GameMode.CREATIVE && message.getId() == inv.getId()) {
            //player.onSlotSet(inv, slot, currentItem);
            player.getServer().getLogger().log(Level.WARNING, "{0} tried to do an invalid inventory action in Creative mode!", new Object[]{player.getName()});
            return false;
        }*/

        if (!Objects.equals(message.getItem(), currentItem)) {
            //player.onSlotSet(inv, slot, currentItem);
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

        if (message.getMode() == 1) {
            if (false /* inv == player.getInventory().getOpenWindow() */) {
                // TODO: if player has e.g. chest open
            } else if (inv == player.getInventory().getCraftingInventory()) {
                // TODO: crafting stuff
            } else {
                if (slot < 9) {
                    for (int i = 9; i < 36; ++i) {
                        if (inv.getItem(i) == null) {
                            // TODO: deal with item stacks
                            inv.setItem(i, currentItem);
                            inv.setItem(slot, null);
                            return true;
                        }
                    }
                } else {
                    for (int i = 0; i < 9; ++i) {
                        if (inv.getItem(i) == null) {
                            // TODO: deal with item stacks
                            inv.setItem(i, currentItem);
                            inv.setItem(slot, null);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        if (inv == player.getInventory().getCraftingInventory() && slot == GlowCraftingInventory.RESULT_SLOT && player.getItemOnCursor() != null) {
            return false;
        }

        inv.setItem(slot, player.getItemOnCursor());
        player.setItemOnCursor(currentItem);

        if (inv == player.getInventory().getCraftingInventory() && slot == GlowCraftingInventory.RESULT_SLOT && currentItem != null) {
            player.getInventory().getCraftingInventory().craft();
        }

        return true;
    }
}
