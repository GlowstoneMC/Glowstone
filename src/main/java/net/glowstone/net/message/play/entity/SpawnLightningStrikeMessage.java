package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class SpawnLightningStrikeMessage implements Message {

    private final int id;
    private final int mode;
    private final double x;
    private final double y;
    private final double z;

    public SpawnLightningStrikeMessage(int id, double x, double y, double z) {
        this(id, 1, x, y, z);
    }

}
