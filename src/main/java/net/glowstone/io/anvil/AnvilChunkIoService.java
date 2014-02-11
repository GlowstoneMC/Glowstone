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
import java.util.*;

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
        CompoundTag root = (CompoundTag) nbt.readTag();
        CompoundTag levelTag = root.getTag("Level", CompoundTag.class);
        nbt.close();

        // read the vertical sections
        List<CompoundTag> sectionList = levelTag.getList("Sections", CompoundTag.class);
        ChunkSection[] sections = new ChunkSection[16];
        for (CompoundTag sectionTag : sectionList) {
            int y = (int) sectionTag.get("Y", ByteTag.class);
            byte[] types = sectionTag.get("Blocks", ByteArrayTag.class);
            byte[] data = sectionTag.get("Data", ByteArrayTag.class);
            byte[] blockLight = sectionTag.get("BlockLight", ByteArrayTag.class);
            byte[] skyLight = sectionTag.get("SkyLight", ByteArrayTag.class);
            sections[y] = new ChunkSection(types, expand(data), expand(skyLight), expand(blockLight));
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.get("TerrainPopulated", ByteTag.class) == 1);

        // read "Biomes" eventually
        // read "Entities" eventually
        // read "HeightMap" if we need to

        // read tile entities
        List<CompoundTag> storedTileEntities = levelTag.getList("TileEntities", CompoundTag.class);
        for (CompoundTag tileEntityTag : storedTileEntities) {
            GlowBlockState state = chunk.getBlock(
                    tileEntityTag.get("x", IntTag.class),
                    tileEntityTag.get("y", IntTag.class),
                    tileEntityTag.get("z", IntTag.class)).getState();
            if (state.getClass() != GlowBlockState.class) {
                BlockStateStore store = BlockStateStoreLookupService.find(tileEntityTag.get("id", StringTag.class));
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

        List<Tag> levelTags = new LinkedList<Tag>();

        // core properties
        levelTags.add(new IntTag("xPos", chunk.getX()));
        levelTags.add(new IntTag("zPos", chunk.getZ()));
        levelTags.add(new ByteTag("TerrainPopulated", (byte) (chunk.isPopulated() ? 1 : 0)));
        levelTags.add(new LongTag("LastUpdate", 0));

        // chunk sections
        List<CompoundTag> sectionTags = new ArrayList<CompoundTag>();
        GlowChunkSnapshot snapshot = chunk.getChunkSnapshot(true, true, false);
        ChunkSection[] sections = snapshot.getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) continue;

            List<Tag> sectionTag = new LinkedList<Tag>();
            sectionTag.add(new ByteTag("Y", i));
            sectionTag.add(new ByteArrayTag("Blocks", sec.types));
            sectionTag.add(new ByteArrayTag("Data", shrink(sec.metaData)));
            sectionTag.add(new ByteArrayTag("BlockLight", shrink(sec.blockLight)));
            sectionTag.add(new ByteArrayTag("SkyLight", shrink(sec.skyLight)));

            sectionTags.add(new CompoundTag("", sectionTag));
        }
        levelTags.add(new ListTag<CompoundTag>("Sections", TagType.COMPOUND, sectionTags));

        // height map
        levelTags.add(new IntArrayTag("HeightMap", snapshot.getRawHeightmap()));

        // biomes
        Biome[] biomesArray = snapshot.getRawBiomes();
        byte[] biomes = new byte[biomesArray.length];
        for (int i = 0; i < biomes.length; ++i) {
            biomes[i] = 0; // todo: convert Biome to value
        }
        levelTags.add(new ByteArrayTag("Biomes", biomes));

        // todo: entities
        List<CompoundTag> entities = new ArrayList<CompoundTag>();
        /* for (Entity entity : chunk.getEntities()) {
            GlowEntity glowEntity = (GlowEntity) entity;
            EntityStore store = EntityStoreLookupService.find(glowEntity.getClass());
            if (store == null)
                continue;
            entities.add(new CompoundTag("", store.save(glowEntity)));
        } */
        levelTags.add(new ListTag<CompoundTag>("Entities", TagType.COMPOUND, entities));

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
        levelTags.add(new ListTag<CompoundTag>("TileEntities", TagType.COMPOUND, tileEntities));

        List<Tag> levelOut = Arrays.<Tag>asList(new CompoundTag("Level", levelTags));

        DataOutputStream out = region.getChunkDataOutputStream(regionX, regionZ);
        NBTOutputStream nbt = new NBTOutputStream(out, false);
        nbt.writeTag(new CompoundTag("", levelOut));
        nbt.close();
    }

    public void unload() throws IOException {
        cache.clear();
    }

}
