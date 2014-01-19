package net.glowstone.net.message.status;

import com.flowpowered.networking.Message;

public final class StatusRequestMessage implements Message {

    @Override
    public boolean isAsync() {
        return false;
    }
}
