package net.glowstone.net.handler.handshake;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.net.ProxyData;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.ProtocolType;

import java.util.logging.Level;

public class HandshakeHandler implements MessageHandler<GlowSession, HandshakeMessage> {

    @Override
    public void handle(GlowSession session, HandshakeMessage message) {
        ProtocolType protocol = ProtocolType.getById(message.getState());
        if (protocol != ProtocolType.LOGIN && protocol != ProtocolType.STATUS) {
            session.disconnect("Invalid state");
            return;
        }

        session.setHostname(message.getAddress() + ":" + message.getPort());

        // Proxies modify the hostname in the HandshakeMessage to contain
        // the client's UUID and (optionally) properties
        if (session.getServer().getProxySupport()) {
            try {
                session.setProxyData(new ProxyData(session, message.getAddress()));
            } catch (IllegalArgumentException ex) {
                session.disconnect("Invalid proxy data provided.");
                // protocol is still set here and below to prevent errors
                // trying to decode packets after this one under the wrong
                // protocol, even though client is kicked
                session.setProtocol(protocol);
                return;
            } catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, "Error parsing proxy data for " + session, ex);
                session.disconnect("Failed to parse proxy data.");
                session.setProtocol(protocol);
                return;
            }
        }

        session.setProtocol(protocol);

        if (protocol == ProtocolType.LOGIN) {
            if (message.getVersion() < GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated client!");
            } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated server!");
            }
        }
    }
}
