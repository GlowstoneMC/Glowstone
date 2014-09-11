package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.TransactionMessage;

public final class TransactionHandler implements MessageHandler<GlowSession, TransactionMessage> {
    @Override
    public void handle(GlowSession session, TransactionMessage message) {
        //GlowServer.logger.info(session + ": " + message);
    }
}
