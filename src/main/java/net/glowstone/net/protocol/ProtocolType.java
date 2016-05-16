package net.glowstone.net.protocol;

/**
 * Enumeration of the different Minecraft protocol states.
 */
public enum ProtocolType {
    HANDSHAKE(new HandshakeProtocol()),
    STATUS(new StatusProtocol()),
    LOGIN(new LoginProtocol()),
    PLAY(new AbstractPlayProtocol.PlayProtocol()),
    PLAY_109(new AbstractPlayProtocol.Play109Protocol()),
    PLAY_107(new AbstractPlayProtocol.Play107Protocol());

    private final GlowProtocol protocol;

    ProtocolType(GlowProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Get a GlowProtocol corresponding to this protocol type.
     *
     * @return A matching GlowProtocol.
     */
    public GlowProtocol getProtocol() {
        return protocol;
    }
}
