package net.glowstone.msg;

import java.util.Arrays;

public final class MultiBlockChangeMessage extends Message {

    private final int chunkX, chunkZ;
    private final short[] coordinates;
    private final byte[] types, metadata;

    public MultiBlockChangeMessage(int chunkX, int chunkZ, short[] coordinates, byte[] types, byte[] metadata) {
        if (coordinates.length != types.length || types.length != metadata.length) {
            throw new IllegalArgumentException();
        }

        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.coordinates = coordinates;
        this.types = types;
        this.metadata = metadata;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int getChanges() {
        return coordinates.length;
    }

    public short[] getCoordinates() {
        return coordinates;
    }

    public byte[] getTypes() {
        return types;
    }

    public byte[] getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "MultiBlockChangeMessage{chunkX=" + chunkX + ",chunkZ=" + chunkZ +
                ",coordinates=" + Arrays.toString(coordinates) +
                ",types=" + Arrays.toString(types) +
                ",metadata=" + Arrays.toString(metadata) + "}";
    }
}
