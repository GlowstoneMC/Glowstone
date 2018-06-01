package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SpawnPositionMessage implements Message {

    private final int x;
    private final int y;
    private final int z;

}
