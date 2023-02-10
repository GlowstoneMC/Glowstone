package net.glowstone.net.message.login;

import com.flowpowered.network.AsyncableMessage;
import lombok.Data;

import java.util.UUID;

@Data
public final class LoginStartMessage implements AsyncableMessage {

    private final String username;
    private final boolean hasUuid;
    private final UUID uuid;

    @Override
    public boolean isAsync() {
        return true;
    }
}
