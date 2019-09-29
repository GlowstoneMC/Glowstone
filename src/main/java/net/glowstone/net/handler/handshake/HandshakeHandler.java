package net.glowstone.net.handler.handshake;

import com.flowpowered.network.MessageHandler;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.ProxyData;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.net.protocol.LoginProtocol;
import net.glowstone.net.protocol.StatusProtocol;

public class HandshakeHandler implements MessageHandler<GlowSession, HandshakeMessage> {
    private final StatusProtocol statusProtocol;
    private final LoginProtocol loginProtocol;

    public HandshakeHandler(StatusProtocol statusProtocol, LoginProtocol loginProtocol) {
        this.statusProtocol = statusProtocol;
        this.loginProtocol = loginProtocol;
    }

    @Override
    public void handle(GlowSession session, HandshakeMessage message) {
        GlowProtocol protocol;
        if (message.getState() == 1) {
            protocol = statusProtocol;
        } else if (message.getState() == 2) {
            protocol = loginProtocol;
        } else {
            session.disconnect("Invalid state");
            return;
        }

        session.setVersion(message.getVersion());
        session.setVirtualHost(InetSocketAddress.createUnresolved(
                message.getAddress(), message.getPort()));

        // Proxies modify the hostname in the HandshakeMessage to contain
        // the client's UUID and (optionally) properties

        session.setProtocol(protocol);

        if (session.getServer().getProxySupport()) {
            try {
                session.setProxyData(new ProxyData(session, message.getAddress()));
            } catch (IllegalArgumentException ex) {
                if (protocol == loginProtocol) {
                    session.disconnect("Invalid proxy data provided.");
                }
                return; // silently ignore parse data in PING protocol
            } catch (Exception ex) {
                if (protocol == loginProtocol) {
                    GlowServer.logger.log(Level.SEVERE,
                            "Error parsing proxy data for " + session, ex);
                    session.disconnect("Failed to parse proxy data.");
                }
                return; // silently ignore parse data in PING protocol
            }
        }

        if (protocol == loginProtocol) {
            if (message.getVersion() < GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated client! I'm running " + GlowServer.GAME_VERSION);
            } else if (message.getVersion() > GlowServer.PROTOCOL_VERSION) {
                session.disconnect("Outdated server! I'm running " + GlowServer.GAME_VERSION);
            }
        }
    }
}
