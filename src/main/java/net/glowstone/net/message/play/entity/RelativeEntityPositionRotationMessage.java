package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class RelativeEntityPositionRotationMessage implements Message {

    private final int id, deltaX, deltaY, deltaZ, rotation, pitch;
    private final boolean onGround;

    public RelativeEntityPositionRotationMessage(int id, int deltaX, int deltaY, int deltaZ, int rotation, int pitch) {
        this(id, deltaX, deltaY, deltaZ, rotation, pitch, true);
    }

}
