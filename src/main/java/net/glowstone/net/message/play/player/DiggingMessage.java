package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class DiggingMessage implements Message {

    public static final int START_DIGGING = 0;
    public static final int CANCEL_DIGGING = 1;
    public static final int FINISH_DIGGING = 2;
    public static final int STATE_DROP_ITEMSTACK = 3;
    public static final int STATE_DROP_ITEM = 4;
    public static final int STATE_SHOT_ARROW_FINISH_EATING = 5;
    public static final int SWAP_ITEM_IN_HAND = 6;

    private final int state;
    private final int x;
    private final int y;
    private final int z;
    private final int face;

}
