package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SpawnPaintingPacket implements Message {

    private final int id;
    private final String title;
    private final int x, y, z, facing;

}
