package net.glowstone.net.handler.login;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.login.LoginPluginResponseMessage;

public class LoginPluginResponseHandler
    implements MessageHandler<GlowSession, LoginPluginResponseMessage> {
    @Override
    public void handle(GlowSession session, LoginPluginResponseMessage message) {
        // nothing to do here
    }
}
