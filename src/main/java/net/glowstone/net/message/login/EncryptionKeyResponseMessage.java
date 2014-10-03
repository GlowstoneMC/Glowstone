package net.glowstone.net.message.login;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class EncryptionKeyResponseMessage implements Message {

    private final byte[] sharedSecret;
    private final byte[] verifyToken;

}
