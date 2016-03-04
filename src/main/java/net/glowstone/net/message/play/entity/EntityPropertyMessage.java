package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.entity.AttributeManager;

import java.util.Map;

@Data
public final class EntityPropertyMessage implements Message {
    private final int id;
    private final Map<String, AttributeManager.Property> properties;
}
