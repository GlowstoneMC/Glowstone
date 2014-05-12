package net.glowstone.io.anvil;

import net.glowstone.GlowChunk;
import net.glowstone.GlowChunk.ChunkSection;
import net.glowstone.GlowChunkSnapshot;
import net.glowstone.GlowServer;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.io.ChunkIoService;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

    /**
     * Reads a chunk from its region file.
     * @param chunk The GlowChunk to read into.
     * @return Whether the
     * @throws IOException if an I/O error occurs.
     */
    public boolean read(GlowChunk chunk) throws IOException {
        int x = chunk.getX(), z = chunk.getZ();
        RegionFile region = cache.getRegionFile(dir, x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);
        if (!region.hasChunk(regionX, regionZ)) {
            return false;
        }

        DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);

        CompoundTag levelTag;
        try (NBTInputStream nbt = new NBTInputStream(in, false)) {
            CompoundTag root = nbt.readCompound();
            levelTag = root.getCompound("Level");
        }

        // read the vertical sections
        List<CompoundTag> sectionList = levelTag.getCompoundList("Sections");
        ChunkSection[] sections = new ChunkSection[16];
        for (CompoundTag sectionTag : sectionList) {
            int y = sectionTag.getByte("Y");
            byte[] types = sectionTag.getByteArray("Blocks");
            byte[] data = sectionTag.getByteArray("Data");
            byte[] blockLight = sectionTag.getByteArray("BlockLight");
            byte[] skyLight = sectionTag.getByteArray("SkyLight");
            sections[y] = new ChunkSection(types, expand(data), expand(skyLight), expand(blockLight));
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.getBool("TerrainPopulated"));

        // read biomes
        if (levelTag.isByteArray("Biomes")) {
            chunk.setBiomes(levelTag.getByteArray("Biomes"));
        }

        // read "Entities" eventually
        // read "HeightMap" if we need to

        // read tile entities
        List<CompoundTag> storedTileEntities = levelTag.getCompoundList("TileEntities");
        for (CompoundTag tileEntityTag : storedTileEntities) {
            TileEntity tileEntity = chunk.getBlock(
                    tileEntityTag.getInt("x"),
                    tileEntityTag.getInt("y"),
                    tileEntityTag.getInt("z")).getTileEntity();
            if (tileEntity != null) {
                try {
                    tileEntity.loadNbt(tileEntityTag);
                } catch (Exception ex) {
                    GlowServer.logger.log(Level.SEVERE, "Error loading TileEntity at " + tileEntity.getBlock(), ex);
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
     * Writes a chunk to its region file.
     * @param chunk The {@link GlowChunk} to write from.
     * @throws IOException if an I/O error occurs.
     */
    public void write(GlowChunk chunk) throws IOException {
        int x = chunk.getX(), z = chunk.getZ();
        RegionFile region = cache.getRegionFile(dir, x, z);
        int regionX = x & (REGION_SIZE - 1);
        int regionZ = z & (REGION_SIZE - 1);

        CompoundTag levelTags = new CompoundTag();

        // core properties
        levelTags.putInt("xPos", chunk.getX());
        levelTags.putInt("zPos", chunk.getZ());
        levelTags.putBool("TerrainPopulated", chunk.isPopulated());
        levelTags.putLong("LastUpdate", 0);

        // chunk sections
        List<CompoundTag> sectionTags = new ArrayList<>();
        GlowChunkSnapshot snapshot = chunk.getChunkSnapshot(true, true, false);
        ChunkSection[] sections = snapshot.getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) continue;

            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putByte("Y", i);
            sectionTag.putByteArray("Blocks", sec.types);
            sectionTag.putByteArray("Data", shrink(sec.metaData));
            sectionTag.putByteArray("BlockLight", shrink(sec.blockLight));
            sectionTag.putByteArray("SkyLight", shrink(sec.skyLight));

            sectionTags.add(sectionTag);
        }
        levelTags.putCompoundList("Sections", sectionTags);

        // height map and biomes
        levelTags.putIntArray("HeightMap", snapshot.getRawHeightmap());
        levelTags.putByteArray("Biomes", snapshot.getRawBiomes());

        // todo: entities
        List<CompoundTag> entities = new ArrayList<>();
        /* for (Entity entity : chunk.getEntities()) {
            GlowEntity glowEntity = (GlowEntity) entity;
            EntityStore store = EntityStoreLookupService.find(glowEntity.getClass());
            if (store == null)
                continue;
            entities.add(new CompoundTag("", store.save(glowEntity)));
        } */
        levelTags.putCompoundList("Entities", entities);

        // tile entities
        List<CompoundTag> tileEntities = new ArrayList<>();
        for (TileEntity entity : chunk.getRawTileEntities()) {
            try {
                CompoundTag tag = new CompoundTag();
                entity.saveNbt(tag);
                tileEntities.add(tag);
            } catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, "Error saving tile entity at " + entity.getBlock(), ex);
            }
        }
        levelTags.putCompoundList("TileEntities", tileEntities);

        CompoundTag levelOut = new CompoundTag();
        levelOut.putCompound("Level", levelTags);

        try (NBTOutputStream nbt = new NBTOutputStream(region.getChunkDataOutputStream(regionX, regionZ), false)) {
            nbt.writeTag(levelOut);
        }
    }

    public void unload() throws IOException {
        cache.clear();
    }

}
