package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SpawnPositionPacket implements Message {

    private final int x, y, z;

}
