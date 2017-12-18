package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class BlockBreakAnimationMessage implements Message {

    private final int id;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final int destroyStage;

}
