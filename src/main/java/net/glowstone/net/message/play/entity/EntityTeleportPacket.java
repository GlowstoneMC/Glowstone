package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class EntityTeleportPacket implements Message {

    private final int id;
    private final double x, y, z;
    private final int rotation, pitch;
    private final boolean onGround;

    public EntityTeleportPacket(int id, double x, double y, double z, int rotation, int pitch) {
        this(id, x, y, z, rotation, pitch, true);
    }

}
