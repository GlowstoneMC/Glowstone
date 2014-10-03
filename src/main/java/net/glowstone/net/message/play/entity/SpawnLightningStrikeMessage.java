package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class SpawnLightningStrikeMessage implements Message {

    private final int id, mode, x, y, z;

    public SpawnLightningStrikeMessage(int id, int x, int y, int z) {
        this(id, 1, x, y, z);
    }

}
