package net.glowstone.net.handler.login;

import com.flowpowered.network.MessageHandler;
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

public final class LoginStartHandler implements MessageHandler<GlowSession, LoginStartMessage> {

    private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Override
    public void handle(GlowSession session, LoginStartMessage message) {
        String name = message.getUsername();

        int length = name.length();
        if (length > 16 || !usernamePattern.matcher(name).find()) {
            session.disconnect("Invalid username provided.", true);
        }

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
                profile = new GlowPlayerProfile(name, uuid, true);
            } else {
                profile = proxy.getProfile(name);
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
