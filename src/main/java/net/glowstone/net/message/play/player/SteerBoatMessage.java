package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class SteerBoatMessage implements Message {

    private final boolean isRightPaddleTurning;
    private final boolean isLeftPaddleTurning;
}
