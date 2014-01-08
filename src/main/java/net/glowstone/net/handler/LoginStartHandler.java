package net.glowstone.net.handler;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.Session;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.util.SecurityUtils;

import java.util.UUID;
import java.util.logging.Level;

public class LoginStartHandler extends MessageHandler<LoginStartMessage> {
    @Override
    public void handle(Session session, GlowPlayer player, LoginStartMessage message) {
        boolean onlineMode = session.getServer().getOnlineMode();
        String username = message.getUsername();

        if (onlineMode) {
            //Get necessary information to create our request message.
            final String sessionId = session.getSessionId();
            final byte[] publicKey = SecurityUtils.generateX509Key(session.getServer().getKeyPair().getPublic()).getEncoded(); //Convert to X509 format
            final byte[] verifyToken = SecurityUtils.generateVerifyToken();

            //Set verify data on session for use in the response handler
            session.setVerifyToken(verifyToken);
            session.setVerifyUsername(message.getUsername());

            //Send created request message and wait for the response
            GlowServer.logger.log(Level.INFO, "Login Start Handler, Session Id: {0}", sessionId);
            session.send(new EncryptionKeyRequestMessage(sessionId, publicKey, verifyToken));
        } else {
            UUID uid = new UUID(0, username.hashCode());
            session.setPlayer(new GlowPlayer(session, username, uid));
        }
    }
}
