package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class PlayParticleMessage implements Message {

    private final int particle;
    private final boolean longDistance;
    private final float x, y, z;
    private final float ofsX, ofsY, ofsZ;
    private final float data;
    private final int count;
    private final int[] extData;

}

