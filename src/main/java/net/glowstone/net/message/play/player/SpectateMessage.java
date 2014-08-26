package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

import java.util.UUID;

public final class SpectateMessage implements Message {

    private final UUID target;

    public SpectateMessage(UUID target) {
        this.target = target;
    }

    public UUID getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "SpectateMessage{" +
                "target=" + target +
                '}';
    }
}

