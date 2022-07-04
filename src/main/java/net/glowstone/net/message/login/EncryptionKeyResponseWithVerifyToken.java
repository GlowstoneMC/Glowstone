package net.glowstone.net.message.login;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class EncryptionKeyResponseWithVerifyToken extends EncryptionKeyResponseMessage {
    private final byte[] verifyToken;

    public EncryptionKeyResponseWithVerifyToken(byte[] sharedSecret, byte[] verifyToken) {
        super(sharedSecret, true);
        this.verifyToken = verifyToken;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
