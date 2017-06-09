package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ClientStatusMessage implements Message {

    public static final int RESPAWN = 0;
    public static final int REQUEST_STATS = 1;

    private final int action;

}

