package net.glowstone.io.mcregion;

import java.io.DataOutputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlockState;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.StorageOperation;
import net.glowstone.io.blockstate.BlockStateStore;
import net.glowstone.io.blockstate.BlockStateStoreLookupService;
import net.glowstone.io.mcregion.region.RegionFile;
import net.glowstone.io.mcregion.region.RegionFileCache;
import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.util.nbt.*;
import org.bukkit.block.BlockState;

/**
 * An implementation of the {@link net.glowstone.io.ChunkIoService} which reads and writes
 * McRegion maps.
 * <p />
 * Information on the McRegion file format can be found on the
 * <a href="http://mojang.com/2011/02/16/minecraft-save-file-format-in-beta-1-3">Mojang</a>
 * blog.
 * @author Graham Edgecombe
 */
public final class McRegionChunkIoService implements ChunkIoService {

    /**
     * The size of a region - a 32x32 group of chunks.
     */
    private static final int REGION_SIZE = 32;

    /**
     * The root directory of the map.
     */
    private File dir;

    /**
     * The region file cache.
     */
    private RegionFileCache cache = new RegionFileCache();

    // TODO: consider the session.lock file

    public McRegionChunkIoService() {
        this(new File("world"));
    }

    public McRegionChunkIoService(File dir) {
        this.dir = dir;
    }

    public boolean read(GlowChunk chunk, int x, int z) throws IOException {
        RegionFile region = cache.getRegionFile(dir, x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);
        if (!region.hasChunk(regionX, regionZ)){
            return false;
        }

        DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);

        NBTInputStream nbt = new NBTInputStream(in, false);
        CompoundTag tag = (CompoundTag) nbt.readTag();
        Map<String, Tag> levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();
        nbt.close();

        byte[] tileData = ((ByteArrayTag) levelTags.get("Blocks")).getValue();
        chunk.initializeTypes(tileData);
        chunk.setPopulated(((ByteTag) levelTags.get("TerrainPopulated")).getValue() == 1);

        byte[] skyLightData = ((ByteArrayTag) levelTags.get("SkyLight")).getValue();
        byte[] blockLightData = ((ByteArrayTag) levelTags.get("BlockLight")).getValue();
        byte[] metaData = ((ByteArrayTag) levelTags.get("Data")).getValue();

        for (int cx = 0; cx < GlowChunk.WIDTH; cx++) {
            for (int cz = 0; cz < GlowChunk.HEIGHT; cz++) {
                for (int cy = 0; cy < GlowChunk.DEPTH; cy++) {
                    boolean mostSignificantNibble = ((cx * GlowChunk.HEIGHT + cz) * GlowChunk.DEPTH + cy) % 2 == 1;
                    int offset = ((cx * GlowChunk.HEIGHT + cz) * GlowChunk.DEPTH + cy) / 2;

                    int skyLight, blockLight, meta;
                    if (mostSignificantNibble) {
                        skyLight = (skyLightData[offset] & 0xF0) >> 4;
                        blockLight = (blockLightData[offset] & 0xF0) >> 4;
                        meta = (metaData[offset] & 0xF0) >> 4;
                    } else {
                        skyLight = skyLightData[offset] & 0x0F;
                        blockLight = blockLightData[offset] & 0x0F;
                        meta = metaData[offset] & 0x0F;
                    }

                    chunk.setSkyLight(cx, cz, cy, skyLight);
                    chunk.setBlockLight(cx, cz, cy, blockLight);
                    chunk.setMetaData(cx, cz, cy, meta);
                }
            }
        }
        List<CompoundTag> storedTileEntities = ((ListTag<CompoundTag>)levelTags.get("TileEntities")).getValue();
        for (CompoundTag tileEntityTag : storedTileEntities) {
            GlowBlockState state = chunk.getBlock(((IntTag)tileEntityTag.getValue().get("x")).getValue(),
                    ((IntTag)tileEntityTag.getValue().get("y")).getValue(), ((IntTag)tileEntityTag.getValue().get("z")).getValue()).getState();
            if (state.getClass() != GlowBlockState.class) {
                BlockStateStore store = BlockStateStoreLookupService.find(((StringTag)tileEntityTag.getValue().get("id")).getValue());
                if (store != null) {
                    store.load(state, tileEntityTag);
                } else {
                    GlowServer.logger.severe("Unable to find store for BlockState " + state.getClass());
                }
            }
        }

        return true;
    }

    /**
     * Writes a chunk. Currently not compatible with the vanilla server.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param chunk The {@link GlowChunk}.
     * @throws IOException
     */
    public void write(int x, int z, GlowChunk chunk) throws IOException {
        RegionFile region = cache.getRegionFile(dir, x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);

        DataOutputStream out = region.getChunkDataOutputStream(regionX, regionZ);
        NBTOutputStream nbt = new NBTOutputStream(out, false);
        Map<String, Tag> levelTags = new HashMap<String, Tag>();

        byte[] skyLightData = new byte[GlowChunk.DEPTH * GlowChunk.WIDTH * GlowChunk.HEIGHT / 2];
        byte[] blockLightData = new byte[GlowChunk.DEPTH * GlowChunk.WIDTH * GlowChunk.HEIGHT / 2];
        byte[] metaData = new byte[GlowChunk.DEPTH * GlowChunk.WIDTH * GlowChunk.HEIGHT / 2];
        byte[] heightMap = new byte[GlowChunk.WIDTH * GlowChunk.HEIGHT];

        for (int cx = 0; cx < GlowChunk.WIDTH; cx++) {
			for (int cz = 0; cz < GlowChunk.HEIGHT; cz++) {
                heightMap[(cx * GlowChunk.HEIGHT + cz) / 2] = (byte)chunk.getWorld().getHighestBlockYAt(x > 0 ? x * cx : cx, z > 0 ? z * cz : cz);
				for (int cy = 0; cy < GlowChunk.DEPTH; cy+=2) {
					int offset = ((cx * GlowChunk.HEIGHT + cz) * GlowChunk.DEPTH + cy) /2;
					skyLightData[offset] = (byte) ((chunk.getSkyLight(cx, cz, cy + 1) << 4) | chunk.getSkyLight(cx, cz, cy));
					blockLightData[offset] = (byte) ((chunk.getBlockLight(cx, cz, cy + 1) << 4) | chunk.getBlockLight(cx, cz, cy));
					metaData[offset] = (byte) ((chunk.getMetaData(cx, cz, cy + 1) << 4) | chunk.getMetaData(cx, cz, cy));
				}
			}
		}

        levelTags.put("Blocks", new ByteArrayTag("Blocks", chunk.getTypes()));
        levelTags.put("SkyLight", new ByteArrayTag("SkyLight", skyLightData));
        levelTags.put("BlockLight", new ByteArrayTag("BlockLight", blockLightData));
        levelTags.put("Data", new ByteArrayTag("Data", metaData));
        levelTags.put("HeightMap", new ByteArrayTag("HeightMap", heightMap));

        levelTags.put("xPos", new IntTag("xPos", chunk.getX()));
        levelTags.put("zPos", new IntTag("zPos", chunk.getZ()));
        levelTags.put("TerrainPopulated", new ByteTag("TerrainPopulated", (byte)(chunk.getPopulated() ? 1 : 0)));

        List<CompoundTag> entities = new ArrayList<CompoundTag>();
        /* for (Entity entity : chunk.getEntities()) {
            GlowEntity glowEntity = (GlowEntity) entity;
            EntityStore store = EntityStoreLookupService.find(glowEntity.getClass());
            if (store == null)
                continue;
            entities.add(new CompoundTag("", store.save(glowEntity)));
        } */
        levelTags.put("Entities", new ListTag<CompoundTag>("Entities", CompoundTag.class, entities)); // TODO: entity storage
        List<CompoundTag> tileEntities = new ArrayList<CompoundTag>();
        for (GlowBlockState state : chunk.getTileEntities()) {
            if (state.getClass() != GlowBlockState.class) {
                BlockStateStore store = BlockStateStoreLookupService.find(state.getClass());
                if (store != null) {
                    tileEntities.add(new CompoundTag("", store.save(state)));
                } else {
                    GlowServer.logger.severe("Unable to find store for BlockState " + state.getClass());
                }
            }
        }
        levelTags.put("TileEntities", new ListTag<CompoundTag>("TileEntities", CompoundTag.class, tileEntities));
        Map<String, Tag> levelOut = new HashMap<String, Tag>();
        levelOut.put("Level", new CompoundTag("Level", levelTags));
        nbt.writeTag(new CompoundTag("", levelOut));
        nbt.close();

    }

}
