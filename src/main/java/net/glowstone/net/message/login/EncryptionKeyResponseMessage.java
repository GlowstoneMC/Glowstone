package net.glowstone.net.message.login;

import com.flowpowered.network.AsyncableMessage;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class EncryptionKeyResponseMessage implements AsyncableMessage {

    private final byte[] sharedSecret;
    private final boolean hasVerifyToken;


    public EncryptionKeyResponseMessage(byte[] sharedSecret, boolean hasVerifyToken) {
        this.sharedSecret = sharedSecret;
        this.hasVerifyToken = hasVerifyToken;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public boolean hasVerifyToken() {
        return hasVerifyToken;
    }
}
