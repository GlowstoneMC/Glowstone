package net.glowstone.msg.handler;

import org.bukkit.inventory.ItemStack;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.msg.TransactionMessage;
import net.glowstone.msg.WindowClickMessage;
import net.glowstone.net.Session;

public final class WindowClickMessageHandler extends MessageHandler<WindowClickMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, WindowClickMessage message) {
		if (player == null)
			return;
        
        int slot = GlowPlayerInventory.networkSlotToInventory(message.getSlot());
        
        GlowPlayerInventory inv = player.getInventory();
        ItemStack currentItem = inv.getItem(slot);
        
        if (currentItem == null) {
            if (message.getItem() != -1) {
                response(session, message, false);
                return;
            }
        } else if (message.getItem() != currentItem.getTypeId() ||
                message.getCount() != currentItem.getAmount() ||
                message.getDamage() != currentItem.getDurability()) {
            response(session, message, false);
            return;
        }
        
        if (message.isShift()) {
            if (false) {
                // TODO: if player has e.g. chest open
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
        
        inv.setItem(slot, player.getItemOnCursor());
        player.setItemOnCursor(currentItem);
	}
    
    private void response(Session session, WindowClickMessage message, boolean success) {
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), success));
    }
    
}
