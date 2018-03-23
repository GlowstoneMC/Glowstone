package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import org.bukkit.event.player.PlayerItemHeldEvent;

public final class HeldItemHandler implements MessageHandler<GlowSession, HeldItemMessage> {

    @Override
    public void handle(GlowSession session, HeldItemMessage message) {
        int slot = message.getSlot();
        if (slot < 0 || slot > 8) {
            // sanity check
            return;
        }

        GlowPlayer player = session.getPlayer();
        int oldSlot = player.getInventory().getHeldItemSlot();
        if (slot == oldSlot) {
            // ignore
            return;
        }

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, oldSlot, slot);
        EventFactory.getInstance().callEvent(event);

        if (!event.isCancelled()) {
            player.getInventory().setRawHeldItemSlot(slot);
        } else {
            // sends a packet to switch back to the previous held slot
            player.getInventory().setHeldItemSlot(oldSlot);
        }
    }
}
