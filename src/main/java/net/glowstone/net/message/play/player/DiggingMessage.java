package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class DiggingMessage implements Message {

    public static final int START_DIGGING = 0;
    public static final int FINISH_DIGGING = 2;
    public static final int STATE_DROP_ITEM = 4;

    private final int state, x, y, z, face;

}
