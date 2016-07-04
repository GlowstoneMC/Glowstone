package net.glowstone.net.handler.handshake;

import com.flowpowered.network.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.ProxyData;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.ProtocolType;

import java.util.logging.Level;

public class HandshakeHandler implements MessageHandler<GlowSession, HandshakeMessage> {

    @Override
    public void handle(GlowSession session, HandshakeMessage message) {
        ProtocolType protocol;
        if (message.getState() == 1) {
            protocol = ProtocolType.STATUS;
        } else if (message.getState() == 2) {
            protocol = ProtocolType.LOGIN;
        } else {
            session.disconnect(GlowServer.lang.getString("invalidState"));
            return;
        }

        session.setVersion(message.getVersion());
        session.setHostname(message.getAddress() + ":" + message.getPort());

        // Proxies modify the hostname in the HandshakeMessage to contain
        // the client's UUID and (optionally) properties
        if (session.getServer().getProxySupport()) {
            try {
                session.setProxyData(new ProxyData(session, message.getAddress()));
            } catch (IllegalArgumentException ex) {
                session.disconnect(GlowServer.lang.getString("invalidProxyData"));
                // protocol is still set here and below to prevent errors
                // trying to decode packets after this one under the wrong
                // protocol, even though client is kicked
                session.setProtocol(protocol);
                return;
            } catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, "Error parsing proxy data for " + session, ex);
                session.disconnect(GlowServer.lang.getString("parseProxyDataFailure"));
                session.setProtocol(protocol);
                return;
            }
        }

        session.setProtocol(protocol);

        if (protocol == ProtocolType.LOGIN) {
            if ((message.getVersion() < GlowServer.LEGACY_PROTOCOL_1_9) || (message.getVersion() == GlowServer.LEGACY_PROTOCOL_1_9 && !session.getServer().canSupportLegacyClients())) {
                session.disconnect(GlowServer.lang.getString("outdatedClient") + GlowServer.GAME_VERSION);
            } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
                session.disconnect(GlowServer.lang.getString("outdatedServer") + GlowServer.GAME_VERSION);
            }
        }
    }
}
