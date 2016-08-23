package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class RelativeEntityPositionPacket implements Message {

    private final int id;
    private final short deltaX, deltaY, deltaZ;
    private final boolean onGround;

    public RelativeEntityPositionPacket(int id, short deltaX, short deltaY, short deltaZ) {
        this(id, deltaX, deltaY, deltaZ, true);
    }

}
