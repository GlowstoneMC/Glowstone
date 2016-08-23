package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PlayParticlePacket implements Message {

    private final int particle;
    private final boolean longDistance;
    private final float x, y, z;
    private final float ofsX, ofsY, ofsZ;
    private final float data;
    private final int count;
    private final int[] extData;

}

