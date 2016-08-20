package net.glowstone.net.event;

import com.flowpowered.network.Message;
import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.session.Session;
import net.glowstone.EventFactory;

public class EventMessageHandler<S extends Session, M extends Message> implements MessageHandler<S, M> {
    @Override
    public void handle(S session, M message) {
        PacketReceiveEvent receiveEvent = new PacketReceiveEvent(message);
        EventFactory.callEvent(receiveEvent);
    }
}
