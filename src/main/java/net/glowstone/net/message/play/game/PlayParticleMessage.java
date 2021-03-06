package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PlayParticleMessage implements Message {

    private final int particle;
    private final boolean longDistance;
    private final double x;
    private final double y;
    private final double z;
    private final float ofsX;
    private final float ofsY;
    private final float ofsZ;
    private final float data;
    private final int count;
    private final Object[] extData;

}

