package net.glowstone.chunk;

import lombok.Data;

@Data
public class SectionPosition {

    private final int x;
    private final int y;
    private final int z;

    public static SectionPosition fromLong(long encoded) {
        int sectionX = (int) (encoded >> 42);
        int sectionY = (int) (encoded << 44 >> 44);
        int sectionZ = (int) (encoded << 22 >> 42);
        return new SectionPosition(sectionX, sectionY, sectionZ);
    }

    public long asLong() {
        return ((x & 0x3FFFFF) << 42) | (y & 0xFFFFF) | ((z & 0x3FFFFF) << 20);
    }
}
