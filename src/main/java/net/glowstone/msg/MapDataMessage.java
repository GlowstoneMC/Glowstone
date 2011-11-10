package net.glowstone.msg;

import java.util.Arrays;

public class MapDataMessage extends Message {
    
    private final short type, id;
    private final byte[] data;
    
    public MapDataMessage(short type, short id, byte[] data) {
        this.type = type;
        this.id = id;
        this.data = data;
    }
    
    public short getType() {
        return type;
    }
    
    public short getId() {
        return id;
    }
    
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MapDataMessage{type=" + type + ",id=" + ",data=" + Arrays.toString(data) + "}";
    }
}
