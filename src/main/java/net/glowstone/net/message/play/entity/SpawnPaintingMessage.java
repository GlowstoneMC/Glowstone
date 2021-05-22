package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.UUID;
import lombok.Data;

@Data
public final class SpawnPaintingMessage implements Message {

    private final int id;
    private final UUID uniqueId;
    private final int artId;
    private final int x;
    private final int y;
    private final int z;
    private final int facing;

}
