package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.EnchantItemMessage;

public final class EnchantItemHandler implements MessageHandler<GlowSession, EnchantItemMessage> {
    @Override
    public void handle(GlowSession session, EnchantItemMessage message) {
        // todo
        GlowServer.logger.info(session + ": " + message);
    }
}
