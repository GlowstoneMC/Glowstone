package net.glowstone.net.message.login;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class EncryptionKeyResponseWithSignature extends EncryptionKeyResponseMessage {
    private final long salt;
    private final byte[] messageSignature;

    public EncryptionKeyResponseWithSignature(byte[] sharedSecret, long salt, byte[] messageSignature) {
        super(sharedSecret, false);
        this.salt = salt;
        this.messageSignature = messageSignature;
    }

    public long getSalt() {
        return salt;
    }

    public byte[] getMessageSignature() {
        return messageSignature;
    }
}
