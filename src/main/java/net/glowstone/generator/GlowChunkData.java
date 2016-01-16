package net.glowstone.generator;

import net.glowstone.GlowChunk;

import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class GlowChunkData implements ChunkData {

    public static final int SECTIONS_SIZE = 16;

    private final int maxHeight;
    private short[][] sections;

    public GlowChunkData(World world) {
        maxHeight = world.getMaxHeight();
        sections = new short[SECTIONS_SIZE][];
    }

    public short[][] getSections() {
        return sections;
    }

    @Override
    public byte getData(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) {
            return (byte) 0;
        }
        if (sections[y >> 4] == null) {
            return (byte) 0;
        }
        return (byte) (sections[y >> 4][((y & 0xF) << 8) | (z << 4) | x] & 0xF);
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
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
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) {
            return 0;
        }
        if (sections[y >> 4] == null) {
            return 0;
        }
        return sections[y >> 4][((y & 0xF) << 8) | (z << 4) | x] >> 4;
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
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH || z >= GlowChunk.WIDTH) {
            return;
        }
        if (sections[y >> 4] == null) {
            sections[y >> 4] = new short[4096];
        }
        sections[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (short) ((blockId << 4) | data);
    }

    @Override
    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, Material material) {
        throw new NotImplementedException("Not implemented yet!");
    }

    @Override
    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, MaterialData materialData) {
        throw new NotImplementedException("Not implemented yet!");
    }

    @Override
    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, int blockId) {
        throw new NotImplementedException("Not implemented yet!");
    }

    @Override
    public void setRegion(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, int blockId, int data) {
        throw new NotImplementedException("Not implemented yet!");
    }
}
