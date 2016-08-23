package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class HealthPacket implements Message {

    private final float health;
    private final int food;
    private final float saturation;

}
