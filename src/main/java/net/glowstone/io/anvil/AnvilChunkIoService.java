package net.glowstone.io.anvil;

import net.glowstone.GlowChunk;
import net.glowstone.GlowChunk.ChunkSection;
import net.glowstone.GlowChunkSnapshot;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlockState;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.blockstate.BlockStateStore;
import net.glowstone.io.blockstate.BlockStateStoreLookupService;
import net.glowstone.util.nbt.*;
import org.bukkit.block.Biome;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes Anvil maps,
 * an improvement on the McRegion file format.
 */
public final class AnvilChunkIoService implements ChunkIoService {

    /**
     * The size of a region - a 32x32 group of chunks.
     */
    private static final int REGION_SIZE = 32;

    /**
     * The root directory of the map.
     */
    private final File dir;

    /**
     * The region file cache.
     */
    private final RegionFileCache cache = new RegionFileCache(".mca");

    // TODO: consider the session.lock file

    public AnvilChunkIoService(File dir) {
        this.dir = dir;
    }

    @SuppressWarnings("unchecked")
    public boolean read(GlowChunk chunk, int x, int z) throws IOException {
        RegionFile region = cache.getRegionFile(dir, x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);
        if (!region.hasChunk(regionX, regionZ)) {
            return false;
        }

        DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);

        NBTInputStream nbt = new NBTInputStream(in, false);
        CompoundTag tag = (CompoundTag) nbt.readTag();
        Map<String, Tag> levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();
        nbt.close();

        // read the vertical sections
        List<CompoundTag> sectionList = ((ListTag<CompoundTag>) levelTags.get("Sections")).getValue();
        ChunkSection[] sections = new ChunkSection[16];
        for (CompoundTag sectionTag : sectionList) {
            int y = ((ByteTag) sectionTag.getValue().get("Y")).getValue();
            byte[] types = ((ByteArrayTag) sectionTag.getValue().get("Blocks")).getValue();
            byte[] data = ((ByteArrayTag) sectionTag.getValue().get("Data")).getValue();
            byte[] blockLight = ((ByteArrayTag) sectionTag.getValue().get("BlockLight")).getValue();
            byte[] skyLight = ((ByteArrayTag) sectionTag.getValue().get("SkyLight")).getValue();
            sections[y] = new ChunkSection(types, expand(data), expand(skyLight), expand(blockLight));
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(((ByteTag) levelTags.get("TerrainPopulated")).getValue() == 1);

        // read "Biomes" eventually
        // read "Entities" eventually
        // read "HeightMap" if we need to

        // read tile entities
        List<CompoundTag> storedTileEntities = ((ListTag<CompoundTag>) levelTags.get("TileEntities")).getValue();
        for (CompoundTag tileEntityTag : storedTileEntities) {
            GlowBlockState state = chunk.getBlock(((IntTag) tileEntityTag.getValue().get("x")).getValue(),
                    ((IntTag) tileEntityTag.getValue().get("y")).getValue(), ((IntTag) tileEntityTag.getValue().get("z")).getValue()).getState();
            if (state.getClass() != GlowBlockState.class) {
                BlockStateStore store = BlockStateStoreLookupService.find(((StringTag) tileEntityTag.getValue().get("id")).getValue());
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
     * Expand a half-length array into a full-length array.
     */
    private byte[] expand(byte[] data) {
        byte[] result = new byte[data.length * 2];
        for (int i = 0; i < data.length; ++i) {
            result[i * 2] = (byte) (data[i] & 0x0f);
            result[i * 2 + 1] = (byte) ((data[i] & 0xf0) >> 4);
        }
        return result;
    }

    /**
     * Shrink a full-length array into a half-length array.
     */
    private byte[] shrink(byte[] data) {
        byte[] result = new byte[data.length / 2];
        for (int i = 0; i < data.length; i += 2) {
            result[i / 2] = (byte) ((data[i + 1] << 4) | data[i]);
        }
        return result;
    }

    /**
     * Writes a chunk. Currently not compatible with the vanilla server.
     *
     * @param x     The X coordinate.
     * @param z     The Z coordinate.
     * @param chunk The {@link net.glowstone.GlowChunk}.
     * @throws java.io.IOException
     */
    public void write(int x, int z, GlowChunk chunk) throws IOException {
        RegionFile region = cache.getRegionFile(dir, x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);

        DataOutputStream out = region.getChunkDataOutputStream(regionX, regionZ);
        NBTOutputStream nbt = new NBTOutputStream(out, false);
        Map<String, Tag> levelTags = new HashMap<String, Tag>();

        // core properties
        levelTags.put("xPos", new IntTag("xPos", chunk.getX()));
        levelTags.put("zPos", new IntTag("zPos", chunk.getZ()));
        levelTags.put("TerrainPopulated", new ByteTag("TerrainPopulated", (byte) (chunk.isPopulated() ? 1 : 0)));
        levelTags.put("LastUpdate", new LongTag("LastUpdate", 0));

        // chunk sections
        List<CompoundTag> sectionTags = new ArrayList<CompoundTag>();
        GlowChunkSnapshot snapshot = chunk.getChunkSnapshot(true, true, false);
        ChunkSection[] sections = snapshot.getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) continue;
            Map<String, Tag> map = new HashMap<String, Tag>();

            map.put("Y", new ByteTag("Y", i));
            map.put("Blocks", new ByteArrayTag("Blocks", sec.types));
            map.put("Data", new ByteArrayTag("Data", shrink(sec.metaData)));
            map.put("BlockLight", new ByteArrayTag("BlockLight", shrink(sec.blockLight)));
            map.put("SkyLight", new ByteArrayTag("SkyLight", shrink(sec.skyLight)));

            sectionTags.add(new CompoundTag("", map));
        }
        levelTags.put("Sections", new ListTag<CompoundTag>("Sections", CompoundTag.class, sectionTags));

        // height map
        levelTags.put("HeightMap", new IntArrayTag("HeightMap", snapshot.getRawHeightmap()));

        // biomes
        Biome[] biomesArray = snapshot.getRawBiomes();
        byte[] biomes = new byte[biomesArray.length];
        for (int i = 0; i < biomes.length; ++i) {
            biomes[i] = 0; // todo: convert Biome to value
        }
        levelTags.put("Biomes", new ByteArrayTag("Biomes", biomes));

        // todo: entities
        List<CompoundTag> entities = new ArrayList<CompoundTag>();
        /* for (Entity entity : chunk.getEntities()) {
            GlowEntity glowEntity = (GlowEntity) entity;
            EntityStore store = EntityStoreLookupService.find(glowEntity.getClass());
            if (store == null)
                continue;
            entities.add(new CompoundTag("", store.save(glowEntity)));
        } */
        levelTags.put("Entities", new ListTag<CompoundTag>("Entities", CompoundTag.class, entities));

        // tile entities
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

    public void unload() throws IOException {
        cache.clear();
    }

}
