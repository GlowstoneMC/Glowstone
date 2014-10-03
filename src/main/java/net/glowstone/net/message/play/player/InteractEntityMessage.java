package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class InteractEntityMessage implements Message {

    private final int id, action;
    private final float targetX, targetY, targetZ;

    public InteractEntityMessage(int id, int action) {
        this(id, action, 0, 0, 0);
    }

    public enum Action {
        INTERACT,
        ATTACK,
        ATTACK_AT
    }
}

