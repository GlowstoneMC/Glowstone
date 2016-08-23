package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.meta.ClientSettings;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.ClientSettingsPacket;

public final class ClientSettingsHandler implements MessageHandler<GlowSession, ClientSettingsPacket> {
    @Override
    public void handle(GlowSession session, ClientSettingsPacket message) {
        session.getPlayer().setSettings(new ClientSettings(message));
    }
}
