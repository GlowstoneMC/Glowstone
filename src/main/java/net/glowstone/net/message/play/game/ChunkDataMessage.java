package net.glowstone.net.message.play.game;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.glowstone.util.nbt.CompoundTag;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

@Getter
public final class ChunkDataMessage extends ChunkLightDataMessage {

    private final CompoundTag heightmap;
    private final ByteBuf data;
    private final Collection<CompoundTag> blockEntities;

    public ChunkDataMessage(int x, int z, CompoundTag heightmap, ByteBuf data, Collection<CompoundTag> blockEntities, boolean trustEdges, BitSet skyLightMask, BitSet blockLightMask, BitSet emptySkyLightMask, BitSet emptyBlockLightMask, List<byte[]> skyLight, List<byte[]> blockLight) {
        super(x, z, trustEdges, skyLightMask, blockLightMask, emptySkyLightMask, emptyBlockLightMask, skyLight, blockLight);
        this.heightmap = heightmap;
        this.data = data;
        this.blockEntities = blockEntities;
    }
}
