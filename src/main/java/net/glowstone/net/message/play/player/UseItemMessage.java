package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class UseItemMessage implements Message {

    public static final int MAIN_HAND = 0;
    public static final int OFF_HAND = 1;

    private final int hand;
}
