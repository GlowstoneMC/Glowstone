package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import net.glowstone.GlowChunk;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ChunkBulkMessage implements Message {

    private final List<ChunkDataMessage> entries = new LinkedList<ChunkDataMessage>();
    private final boolean skyLight;

    public ChunkBulkMessage(boolean skyLight, Collection<GlowChunk> chunks) {
        this.skyLight = skyLight;

        for (GlowChunk chunk : chunks) {
            entries.add(chunk.toMessage(skyLight, true, 0));
        }
    }

    public boolean getSkyLight() {
        return skyLight;
    }

    public List<ChunkDataMessage> getEntries() {
        return entries;
    }
}
