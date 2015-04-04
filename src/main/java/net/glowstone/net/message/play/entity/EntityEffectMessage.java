package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class EntityEffectMessage implements Message {

    private final int id;
    private final int effect, amplifier;
    private final int duration;
    private final boolean hideParticles;

    public EntityEffectMessage(int id, int type, int amplifier, int duration, boolean ambient)  {

    }
}
