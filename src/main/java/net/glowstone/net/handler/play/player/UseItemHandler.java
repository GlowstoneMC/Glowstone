package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;

public class UseItemHandler implements MessageHandler<GlowSession, UseItemPacket> {
    @Override
    public void handle(GlowSession session, UseItemPacket message) {
        //TODO: Implement item handling (Sent when pressing the Use Item key (default: right click) with an item in hand.)
    }
}
