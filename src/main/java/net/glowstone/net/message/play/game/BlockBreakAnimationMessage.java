package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class BlockBreakAnimationMessage implements Message {

    private final int id, x, y, z, destroyStage;

}
