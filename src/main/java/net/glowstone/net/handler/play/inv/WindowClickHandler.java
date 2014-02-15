package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.session.Session;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowCraftingInventory;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.logging.Level;

public final class WindowClickHandler implements MessageHandler<GlowSession, WindowClickMessage> {
    public void handle(GlowSession session, WindowClickMessage message) {
        final GlowPlayer player = session.getPlayer();

        GlowInventory inv = player.getInventory();
        int slot = inv.getItemSlot(message.getSlot());

        // Modify slot if needed
        if (slot < 0) {
            inv = player.getInventory().getCraftingInventory();
            slot = inv.getItemSlot(message.getSlot());
        }
        if (slot == -1) {
            player.setItemOnCursor(null);
            response(session, message, true);
        }
        if (slot < 0) {
            response(session, message, false);
            player.getServer().getLogger().log(Level.WARNING, "Got invalid inventory slot {0} from {1}", new Object[]{message.getSlot(), player.getName()});
            return;
        }

        ItemStack currentItem = inv.getItem(slot);

        if (player.getGameMode() == GameMode.CREATIVE && message.getId() == inv.getId()) {
            response(session, message, false);
            player.onSlotSet(inv, slot, currentItem);
            player.getServer().getLogger().log(Level.WARNING, "{0} tried to do an invalid inventory action in Creative mode!", new Object[]{player.getName()});
            return;
        }
        if (!Objects.equals(message.getItem(), currentItem)) {
            player.onSlotSet(inv, slot, currentItem);
            response(session, message, false);
            return;
        }

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
                            response(session, message, true);
                            return;
                        }
                    }
                } else {
                    for (int i = 0; i < 9; ++i) {
                        if (inv.getItem(i) == null) {
                            // TODO: deal with item stacks
                            inv.setItem(i, currentItem);
                            inv.setItem(slot, null);
                            response(session, message, true);
                            return;
                        }
                    }
                }
            }
            response(session, message, false);
            return;
        }

        if (inv == player.getInventory().getCraftingInventory() && slot == GlowCraftingInventory.RESULT_SLOT && player.getItemOnCursor() != null) {
            response(session, message, false);
            return;
        }

        response(session, message, true);
        inv.setItem(slot, player.getItemOnCursor());
        player.setItemOnCursor(currentItem);

        if (inv == player.getInventory().getCraftingInventory() && slot == GlowCraftingInventory.RESULT_SLOT && currentItem != null) {
            player.getInventory().getCraftingInventory().craft();
        }
    }

    private void response(Session session, WindowClickMessage message, boolean success) {
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), success));
    }
}
