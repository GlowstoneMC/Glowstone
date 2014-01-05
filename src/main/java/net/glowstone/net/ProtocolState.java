package net.glowstone.net;

/**
 * The state the protocol is in, determining the opcode-to-message mapping.
 */
public enum ProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY
}
