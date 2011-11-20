package net.glowstone.msg.handler;

import java.util.logging.Level;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.KickMessage;
import net.glowstone.net.Session;

public final class KickMessageHandler extends MessageHandler<KickMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, KickMessage message) {
        session.disconnect("Goodbye!", true);
    }

}
