package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class EntityRotationPacket implements Message {

    private final int id, rotation, pitch;
    private final boolean onGround;

    public EntityRotationPacket(int id, int rotation, int pitch) {
        this(id, rotation, pitch, true);
    }

}
