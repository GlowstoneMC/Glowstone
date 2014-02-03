package net.glowstone.net.protocol;

import net.glowstone.GlowServer;
import net.glowstone.net.codec.handshake.HandshakeCodec;
import net.glowstone.net.handler.handshake.HandshakeHandler;
import net.glowstone.net.message.handshake.HandshakeMessage;

public final class HandshakeProtocol extends GlowProtocol {
    public HandshakeProtocol(GlowServer server) {
        super(server, "HANDSHAKE", 0);
        inbound(0x00, HandshakeMessage.class, HandshakeCodec.class, HandshakeHandler.class);
    }
}
