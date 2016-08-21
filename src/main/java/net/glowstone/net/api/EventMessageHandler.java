package net.glowstone.net.api;

import com.flowpowered.network.Message;
import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.net.GlowSession;

public class EventMessageHandler<M extends Message> implements MessageHandler<GlowSession, M> {
    @Override
    public void handle(GlowSession session, M message) {
        PacketReceiveEvent receiveEvent = new PacketReceiveEvent(message, session, session.getPlayer());
        EventFactory.callEvent(receiveEvent);
    }
}
