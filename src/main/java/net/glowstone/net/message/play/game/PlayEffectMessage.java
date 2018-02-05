package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PlayEffectMessage implements Message {

    private final int id;
    private final int x;
    private final int y;
    private final int z;
    private final int data;
    private final boolean ignoreDistance;

}
