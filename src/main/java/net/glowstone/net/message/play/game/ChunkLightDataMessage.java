package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.util.nbt.CompoundTag;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

@Data
public class ChunkLightDataMessage implements Message {

    private final int x;
    private final int z;
    private final boolean trustEdges;
    private final BitSet skyLightMask;
    private final BitSet blockLightMask;
    private final BitSet emptySkyLightMask;
    private final BitSet emptyBlockLightMask;
    private final List<byte[]> skyLight; // TODO other structure
    private final List<byte[]> blockLight;

}
