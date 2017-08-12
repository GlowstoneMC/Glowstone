package net.glowstone.net.handler.handshake;

import com.flowpowered.network.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.ProxyData;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.ProtocolType;
import net.glowstone.util.lang.I;

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
            session.disconnect(I.tr("server.invalid.state"));
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
                session.disconnect(I.tr("server.invalid.proxy.data"));
                // protocol is still set here and below to prevent errors
                // trying to decode packets after this one under the wrong
                // protocol, even though client is kicked
                session.setProtocol(protocol);
                return;
            } catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, I.tr("server.invalid.proxy.parsing", session), ex);
                session.disconnect(I.tr("server.invalid.proxy.failed"));
                session.setProtocol(protocol);
                return;
            }
        }

        session.setProtocol(protocol);

        if (protocol == ProtocolType.LOGIN) {
            if (message.getVersion() < GlowServer.PROTOCOL_VERSION) {
                session.disconnect(I.tr("server.outdated.client", GlowServer.GAME_VERSION));
            } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
                session.disconnect(I.tr("server.outdated.server", GlowServer.GAME_VERSION));
            }
        }
    }
}
