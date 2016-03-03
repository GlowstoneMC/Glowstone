package net.glowstone.net.message.status;

import com.flowpowered.network.AsyncableMessage;
import lombok.Data;

@Data
public final class StatusRequestMessage implements AsyncableMessage {

    @Override
    public boolean isAsync() {
        return true;
    }

}
