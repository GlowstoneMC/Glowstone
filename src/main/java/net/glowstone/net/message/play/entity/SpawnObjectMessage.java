package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class SpawnObjectMessage implements Message {

    private final int id, type, x, y, z, pitch, yaw, data, velX, velY, velZ;

    public SpawnObjectMessage(int id, int type, int x, int y, int z, int pitch, int yaw) {
        this(id, type, x, y, z, pitch, yaw, 0, 0, 0, 0);
    }

    public boolean hasFireball() {
        return data != 0;
    }

}
