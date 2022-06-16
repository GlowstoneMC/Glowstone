package net.glowstone.generator;

import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.block.data.BlockDataManager;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.util.MaterialUtil;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class GlowChunkData implements ChunkData {

    @Getter
    private final int maxHeight;
    @Getter
    private int[][] sections;

    public GlowChunkData(World world) {
        maxHeight = world.getMaxHeight();
        sections = new int[GlowChunk.SEC_COUNT][];
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

    @NotNull
    @Override
    public Material getType(int x, int y, int z) {
        BlockDataManager blockDataManager = ((GlowServer) Bukkit.getServer()).getBlockDataManager();
        return blockDataManager.convertToBlockData(getTypeId(x, y, z)).getMaterial();
    }

    @NotNull
    @Override
    public MaterialData getTypeAndData(int x, int y, int z) {
        return getType(x, y, z).getNewData(getData(x, y, z));
    }

    @NotNull
    @Override
    public BlockData getBlockData(int x, int y, int z) {
        // TODO: 1.13
        throw new NotImplementedException();
    }

    private int getTypeId(int x, int y, int z) {
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
        setBlock(x, y, z, MaterialUtil.getId(material));
    }

    @Override
    public void setBlock(int x, int y, int z, MaterialData materialData) {
        setBlock(x, y, z, MaterialUtil.getId(materialData.getItemType()), (byte) 0);
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull BlockData blockData) {
        // TODO: 1.13
        throw new NotImplementedException();
    }

    public void setBlock(int x, int y, int z, int blockId) {
        setBlock(x, y, z, blockId, (byte) 0);
    }

    public void setBlock(int x, int y, int z, int blockId, byte data) {
        if (x < 0 || y < 0 || z < 0 || x >= GlowChunk.HEIGHT || y >= GlowChunk.DEPTH
            || z >= GlowChunk.WIDTH) {
            return;
        }
        if (sections[y >> 4] == null) {
            sections[y >> 4] = new int[4096];
        }
        sections[y >> 4][(y & 0xF) << 8 | z << 4 | x] = (short) (blockId << 4 | data);
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @NotNull Material material) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    setBlock(x, y, z, material);
                }
            }
        }
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @NotNull MaterialData materialData) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    setBlock(x, y, z, materialData);
                }
            }
        }
    }

    @Override
    public void setRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, @NotNull BlockData blockData) {
        // TODO: 1.13
        throw new NotImplementedException();
    }
}
