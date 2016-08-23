package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.WindowClosePacket;

public final class CloseWindowHandler implements MessageHandler<GlowSession, WindowClosePacket> {
    @Override
    public void handle(GlowSession session, WindowClosePacket message) {
        // closing the inventory will drop any items as needed
        session.getPlayer().closeInventory();
    }
}
