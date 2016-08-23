package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class InteractEntityPacket implements Message {

    private final int id, action;
    private final float targetX, targetY, targetZ;
    private final int hand; // 0 = main hand, 1 = off hand
    public InteractEntityPacket(int id, int action) {
        this(id, action, 0, 0, 0, 0);
    }

    public InteractEntityPacket(int id, int action, int hand) {
        this(id, action, 0, 0, 0, hand);
    }

    public enum Action {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }
}

