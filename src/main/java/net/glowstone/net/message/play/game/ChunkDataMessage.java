package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.util.nbt.CompoundTag;

@Data
public final class ChunkDataMessage implements Message {

    private final int x, z;
    private final boolean continuous;
    private final int primaryMask;
    private final ByteBuf data;
    private final CompoundTag[] tileEntities;

}
