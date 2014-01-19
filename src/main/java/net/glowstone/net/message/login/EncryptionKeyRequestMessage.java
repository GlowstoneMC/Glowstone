package net.glowstone.net.message.login;

import com.flowpowered.networking.Message;

public final class EncryptionKeyRequestMessage implements Message {

    private final String sessionId;
    private final byte[] publicKey;
    private final byte[] verifyToken;

    public EncryptionKeyRequestMessage(String sessionId, byte[] publicKey, byte[] verifyToken) {
        this.sessionId = sessionId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
