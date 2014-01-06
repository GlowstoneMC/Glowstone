package net.glowstone.net.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.login.LoginStartMessage;

import java.util.UUID;

public class LoginStartHandler extends MessageHandler<LoginStartMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, LoginStartMessage message) {
        boolean onlineMode = session.getServer().getOnlineMode();
        String username = message.getUsername();

        if (onlineMode) {
            // perform encryption and client authentication setup here
            session.disconnect("Online mode unavailable");
        } else {
            UUID uid = new UUID(0, username.hashCode());
            session.setPlayer(new GlowPlayer(session, username, uid));
        }
    }
}
