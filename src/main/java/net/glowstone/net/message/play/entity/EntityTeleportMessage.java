package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class EntityTeleportMessage implements Message {

    private final int id, x, y, z, rotation, pitch;
    private final boolean onGround;

    public EntityTeleportMessage(int id, int x, int y, int z, int rotation, int pitch) {
        this(id, x, y, z, rotation, pitch, true);
    }

}
