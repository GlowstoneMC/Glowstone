package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.TeleportConfirmPacket;

public class TeleportConfirmHandler implements MessageHandler<GlowSession, TeleportConfirmPacket> {
    @Override
    public void handle(GlowSession session, TeleportConfirmPacket message) {
        //TODO: Handle this
    }
}
