package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.ActivateItemMessage;
import net.glowstone.net.Session;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class ActivateItemMessageHandler extends MessageHandler<ActivateItemMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, ActivateItemMessage message) {
        if (player == null)
            return;

        if (message.getSlot() < 0 || message.getSlot() > 8) // sanity check
            return;
        
        player.getInventory().setHeldItemSlot(message.getSlot());
    }

}
