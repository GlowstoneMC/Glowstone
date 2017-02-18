package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class AnimateEntityMessage implements Message {

    public static final int SWING_MAIN_HAND = 0;
    public static final int TAKE_DAMAGE = 1;
    public static final int LEAVE_BED = 2;
    public static final int SWING_OFF_HAND = 3;
    public static final int CRIT = 4;
    public static final int MAGIC_CRIT = 5;

    private final int id, animation;

}
