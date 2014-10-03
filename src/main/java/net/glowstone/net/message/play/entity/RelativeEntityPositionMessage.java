package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class RelativeEntityPositionMessage implements Message {

    private final int id, deltaX, deltaY, deltaZ;
    private final boolean onGround;

    public RelativeEntityPositionMessage(int id, int deltaX, int deltaY, int deltaZ) {
        this(id, deltaX, deltaY, deltaZ, true);
    }

}
