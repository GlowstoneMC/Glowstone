package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class EntityTeleportMessage implements Message {

    private final int id;
    private final double x, y, z;
    private final int rotation, pitch;
    private final boolean onGround;

    public EntityTeleportMessage(int id, double x, double y, double z, int rotation, int pitch) {
        this(id, x, y, z, rotation, pitch, true);
    }

}
