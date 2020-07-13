package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.UpdateCommandBlockMinecartMessage;

public class UpdateCommandBlockMinecartHandler implements MessageHandler<GlowSession, UpdateCommandBlockMinecartMessage> {
    @Override
    public void handle(GlowSession session, UpdateCommandBlockMinecartMessage message) {
        //TODO: handle packet
    }
}
