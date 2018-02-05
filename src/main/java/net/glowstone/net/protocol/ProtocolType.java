package net.glowstone.net.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of the different Minecraft protocol states.
 */
@Getter
@RequiredArgsConstructor
public enum ProtocolType {
    HANDSHAKE(new HandshakeProtocol()),
    STATUS(new StatusProtocol()),
    LOGIN(new LoginProtocol()),
    PLAY(new PlayProtocol());

    /**
     * Get a GlowProtocol corresponding to this protocol type.
     *
     * @return A matching GlowProtocol.
     */
    private final GlowProtocol protocol;
}
