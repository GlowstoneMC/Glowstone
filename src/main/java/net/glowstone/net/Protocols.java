package net.glowstone.net;

/**
 * The state the protocol is in, determining the opcode-to-message mapping.
 */
public enum Protocols {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY;
}
