package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public final class SpawnObjectMessage implements Message {

    public static final int ITEM = 2;

    private final int id;
    private final UUID uuid; //TODO: Handle UUID
    private final int type, x, y, z, pitch, yaw, data, velX, velY, velZ;

    public SpawnObjectMessage(int id, UUID uuid, int type, int x, int y, int z, int pitch, int yaw) {
        this(id, uuid, type, x, y, z, pitch, yaw, 0, 0, 0, 0);
    }

    public boolean hasFireball() {
        return data != 0;
    }

}
