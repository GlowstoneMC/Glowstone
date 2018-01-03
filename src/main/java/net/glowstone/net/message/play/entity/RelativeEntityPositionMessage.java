package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class RelativeEntityPositionMessage implements Message {

    private final int id;
    private final short deltaX;
    private final short deltaY;
    private final short deltaZ;
    private final boolean onGround;

    public RelativeEntityPositionMessage(int id, short deltaX, short deltaY, short deltaZ) {
        this(id, deltaX, deltaY, deltaZ, true);
    }

}
