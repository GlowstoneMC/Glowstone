package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class RelativeEntityPositionRotationMessage implements Message {

    private final int id;
    private final short deltaX;
    private final short deltaY;
    private final short deltaZ;
    private final int rotation;
    private final int pitch;
    private final boolean onGround;

    public RelativeEntityPositionRotationMessage(int id, short deltaX, short deltaY, short deltaZ,
        int rotation, int pitch) {
        this(id, deltaX, deltaY, deltaZ, rotation, pitch, true);
    }

}
