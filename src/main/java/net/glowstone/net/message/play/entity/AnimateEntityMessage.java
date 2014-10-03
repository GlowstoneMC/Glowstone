package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class AnimateEntityMessage implements Message {

    public static final int IN_SWING_ARM = 1;
    public static final int IN_HURT = 2;
    public static final int IN_LEAVE_BED = 3;

    public static final int OUT_SWING_ARM = 0;
    public static final int OUT_HURT = 1;
    public static final int OUT_LEAVE_BED = 2;

    private final int id, animation;

}
