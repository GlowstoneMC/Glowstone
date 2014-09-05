package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.LinkedList;
import java.util.List;

public final class ChunkBulkMessage implements Message {

    private final boolean skyLight;
    private final List<ChunkDataMessage> entries = new LinkedList<>();

    public ChunkBulkMessage(boolean skyLight, List<ChunkDataMessage> messages) {
        this.skyLight = skyLight;
        this.entries.addAll(messages);
    }

    public boolean getSkyLight() {
        return skyLight;
    }

    public List<ChunkDataMessage> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return "ChunkBulkMessage{" +
                "skyLight=" + skyLight +
                ", entries=(" + entries.size() + ")" + entries +
                '}';
    }
}
