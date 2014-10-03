package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class SpawnXpOrbMessage implements Message {

    private final int id, x, y, z;
    private final short count;

}
