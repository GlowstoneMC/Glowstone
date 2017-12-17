package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.UUID;
import lombok.Data;

@Data
public final class SpawnPaintingMessage implements Message {

    private final int id;
    private final UUID uniqueId;
    private final String title;
    private final int x, y, z, facing;

}
