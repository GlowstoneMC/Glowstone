package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class EntityHeadRotationMessage implements Message {

    private final int id;
    private final int rotation;

    public EntityHeadRotationMessage(int id, int yaw) {
        this.id = id;
        this.rotation = yaw;
    }

}

