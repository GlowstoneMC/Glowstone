package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class UpdateStructureBlockMessage implements Message {
    private final int x;
    private final int y;
    private final int z;
    private final int action;
    private final int mode;
    private final String name;
    private final byte offsetX;
    private final byte offsetY;
    private final byte offsetZ;
    private final byte sizeX;
    private final byte sizeY;
    private final byte sizeZ;
    private final int mirror;
    private final int rotation;
    private final String metadata;
    private final float integrity;
    private final long seed;
    private final boolean ignoreEntities;
    private final boolean showAir;
    private final boolean showBoundingBox;
}
