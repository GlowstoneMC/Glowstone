package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class HealthMessage implements Message {

    private final float health;
    private final int food;
    private final float saturation;

}
