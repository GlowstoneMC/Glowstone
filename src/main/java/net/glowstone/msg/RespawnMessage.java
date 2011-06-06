package net.glowstone.msg;

public final class RespawnMessage extends Message {

    private final byte dimension;
    
    public RespawnMessage(byte dimension) {
        this.dimension = dimension;
    }
    
    public byte getDimension() {
        return dimension;
    }
    
}
