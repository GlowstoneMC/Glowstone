package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.meta.ClientSettings;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.ClientSettingsMessage;

public final class ClientSettingsHandler implements
    MessageHandler<GlowSession, ClientSettingsMessage> {

    @Override
    public void handle(GlowSession session, ClientSettingsMessage message) {
        session.getPlayer().setSettings(new ClientSettings(message));
    }
}
