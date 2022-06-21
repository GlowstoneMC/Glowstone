package net.glowstone.net.message.login;

import com.flowpowered.network.AsyncableMessage;
import lombok.Data;

@Data
public final class LoginStartMessage implements AsyncableMessage {

    private final String username;
    private final long timestamp;
    private final byte[] publicKey;
    private final byte[] signature;

    @Override
    public boolean isAsync() {
        return true;
    }
}
