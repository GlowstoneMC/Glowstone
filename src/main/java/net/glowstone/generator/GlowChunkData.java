package net.glowstone.generator;

import lombok.Getter;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class GlowChunkData implements ChunkData {

    @Getter
    private final int maxHeight;
    @Getter
    private short[][] sections;

    public GlowChunkData(World world) {
        maxHeight = world.getMaxHeight();
        sections = new short[GlowChunk.SEC_COUNT][];
    }

    @Override
    public byte getData(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH
            || z >= GlowChunk.WIDTH) {
            return (byte) 0;
        }
        if (sections[y >> 4] == null) {
            return (byte) 0;
        }
        return (byte) (sections[y >> 4][(y & 0xF) << 8 | z << 4 | x] & 0xF);
    }

    @Override
    public Material getType(int x, int y, int z) {
        return Material.getMaterial(getTypeId(x, y, z));
    }

    @Override
    public MaterialData getTypeAndData(int x, int y, int z) {
        return getType(x, y, z).getNewData(getData(x, y, z));
    }

    @Override
    public int getTypeId(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH
            || z >= GlowChunk.WIDTH) {
            return 0;
        }
        if (sections[y >> 4] == null) {
            return 0;
        }
        return sections[y >> 4][(y & 0xF) << 8 | z << 4 | x] >> 4;
    }

    @Override
    public void setBlock(int x, int y, int z, Material material) {
        setBlock(x, y, z, material.getId());
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialData materialData) {
        setBlock(x, y, z, materialData.getItemTypeId(), materialData.getData());
    }

    @Override
    public void setBlock(int x, int y, int z, int blockId) {
        setBlock(x, y, z, blockId, (byte) 0);
    }

    @Override
    public void setBlock(int x, int y, int z, int blockId, byte data) {
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH
            || z >= GlowChunk.WIDTH) {
            return;
        }
        if (sections[y >> 4] == null) {
            sections[y >> 4] = new short[4096];
        }
        sections[y >> 4][(y & 0xF) << 8 | z << 4 | x] = (short) (blockId << 4 | data);
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
        Material material) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
        MaterialData materialData) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int blockId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int blockId,
        int data) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
