package net.glowstone.net.handler.login;

import com.flowpowered.network.MessageHandler;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.net.GlowSession;
import net.glowstone.net.ProxyData;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.util.SecurityUtils;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public final class LoginStartHandler implements MessageHandler<GlowSession, LoginStartMessage> {

    @Override
    public void handle(GlowSession session, LoginStartMessage message) {
        String name = message.getUsername();
        GlowServer server = session.getServer();

        if (server.getOnlineMode()) {
            // Get necessary information to create our request message
            String sessionId = session.getSessionId();
            byte[] publicKey = SecurityUtils
                .generateX509Key(server.getKeyPair().getPublic())
                .getEncoded(); //Convert to X509 format
            byte[] verifyToken = SecurityUtils.generateVerifyToken();

            // Set verify data on session for use in the response handler
            session.setVerifyToken(verifyToken);
            session.setVerifyUsername(name);

            // Send created request message and wait for the response
            session.send(new EncryptionKeyRequestMessage(sessionId, publicKey, verifyToken));
        } else {
            GlowPlayerProfile profile;
            ProxyData proxy = session.getProxyData();

            if (proxy == null) {
                UUID uuid = UUID
                    .nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
                profile = new GlowPlayerProfile(name, uuid);
            } else {
                profile = proxy.getProfile();
                if (profile == null) {
                    profile = proxy.getProfile(name);
                }
            }

            AsyncPlayerPreLoginEvent event = EventFactory.getInstance()
                .onPlayerPreLogin(profile.getName(), session.getAddress(), profile.getId());
            if (event.getLoginResult() != Result.ALLOWED) {
                session.disconnect(event.getKickMessage(), true);
                return;
            }

            GlowPlayerProfile finalProfile = profile;
            server.getScheduler().runTask(null, () -> session.setPlayer(finalProfile));
        }
    }
}
