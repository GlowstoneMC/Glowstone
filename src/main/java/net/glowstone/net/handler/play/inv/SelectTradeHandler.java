package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.SelectTradeMessage;

public final class SelectTradeHandler implements MessageHandler<GlowSession, SelectTradeMessage> {
    @Override
    public void handle(GlowSession session, SelectTradeMessage message) {
        //TODO: handle packet
    }
}
