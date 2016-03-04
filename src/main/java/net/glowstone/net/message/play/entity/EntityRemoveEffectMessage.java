package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class EntityRemoveEffectMessage implements Message {

    private final int id;
    private final int effect;

}
