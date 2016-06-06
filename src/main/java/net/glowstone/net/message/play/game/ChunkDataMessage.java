package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.util.nbt.CompoundTag;

@Data
public final class ChunkDataMessage extends ChunkDataLegacyMessage implements Message {

    private final CompoundTag[] tileEntities;

    public ChunkDataMessage(int x, int z, boolean continuous, int primaryMask, ByteBuf data, CompoundTag[] tileEntities) {
        super(x, z, continuous, primaryMask, data);
        this.tileEntities = tileEntities;
    }

    public ChunkDataLegacyMessage toLegacy() {
        return new ChunkDataLegacyMessage(getX(), getZ(), isContinuous(), getPrimaryMask(), getData());
    }

}
