package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.Arrays;

public final class MapDataMessage implements Message {
    
    private final short id;
    private final byte[] data;
    
    public MapDataMessage(short id, byte[] data) {
        this.id = id;
        this.data = data;
    }
    
    public short getId() {
        return id;
    }
    
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MapDataMessage{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
