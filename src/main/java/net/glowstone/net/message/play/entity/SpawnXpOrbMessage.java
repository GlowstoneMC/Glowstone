package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SpawnXpOrbMessage implements Message {

    private final int id;
    private final double x, y, z;
    private final short count;

}
