package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class EntityEffectMessage implements Message {

    private final int id;
    private final int effect;
    private final int amplifier;
    private final int duration;
    private final boolean hideParticles;

}
