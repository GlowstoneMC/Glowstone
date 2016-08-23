package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.TransactionPacket;

public final class TransactionHandler implements MessageHandler<GlowSession, TransactionPacket> {
    @Override
    public void handle(GlowSession session, TransactionPacket message) {
        //GlowServer.logger.info(session + ": " + message);
    }
}
