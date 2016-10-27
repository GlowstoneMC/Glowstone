package net.glowstone.net.message.handshake;

import com.flowpowered.network.AsyncableMessage;
import lombok.Data;

@Data
public final class HandshakeMessage implements AsyncableMessage {

    private final int version;
    private final String address;
    private final int port;
    private final int state;

    @Override
    public boolean isAsync() {
        return true;
    }

}
