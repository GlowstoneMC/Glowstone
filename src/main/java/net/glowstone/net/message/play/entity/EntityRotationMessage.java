package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class EntityRotationMessage implements Message {

    private final int id;
    private final int rotation;
    private final int pitch;
    private final boolean onGround;

    public EntityRotationMessage(int id, int rotation, int pitch) {
        this(id, rotation, pitch, true);
    }

}
