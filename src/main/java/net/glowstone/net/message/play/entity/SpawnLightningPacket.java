package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class SpawnLightningPacket implements Message {

    private final int id, mode;
    private final double x, y, z;

    public SpawnLightningPacket(int id, double x, double y, double z) {
        this(id, 1, x, y, z);
    }

}
