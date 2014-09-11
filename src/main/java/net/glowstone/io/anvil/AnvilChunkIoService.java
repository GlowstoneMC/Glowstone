package net.glowstone.io.anvil;

import net.glowstone.GlowChunk;
import net.glowstone.GlowChunk.ChunkSection;
import net.glowstone.GlowChunkSnapshot;
import net.glowstone.GlowServer;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.NibbleArray;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import net.glowstone.util.nbt.TagType;

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

    // todo: consider the session.lock file

    public AnvilChunkIoService(File dir) {
        this.dir = dir;
    }

    /**
     * Reads a chunk from its region file.
     * @param chunk The GlowChunk to read into.
     * @return Whether the
     * @throws IOException if an I/O error occurs.
     */
    @Override
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
            byte[] rawTypes = sectionTag.getByteArray("Blocks");
            NibbleArray extTypes = sectionTag.containsKey("Add") ? new NibbleArray(sectionTag.getByteArray("Add")) : null;
            NibbleArray data = new NibbleArray(sectionTag.getByteArray("Data"));
            NibbleArray blockLight = new NibbleArray(sectionTag.getByteArray("BlockLight"));
            NibbleArray skyLight = new NibbleArray(sectionTag.getByteArray("SkyLight"));

            char[] types = new char[rawTypes.length];
            for (int i = 0; i < rawTypes.length; i++) {
                types[i] = (char) (((extTypes == null ? 0 : extTypes.get(i)) << 12) | ((rawTypes[i] & 0xff) << 4) | data.get(i));
            }
            sections[y] = new ChunkSection(types, skyLight, blockLight);
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.getBool("TerrainPopulated"));

        // read biomes
        if (levelTag.isByteArray("Biomes")) {
            chunk.setBiomes(levelTag.getByteArray("Biomes"));
        }

        // read entities
        if (levelTag.isList("Entities", TagType.COMPOUND)) {
            for (CompoundTag entityTag : levelTag.getCompoundList("Entities")) {
                try {
                    // note that creating the entity is sufficient to add it to the world
                    EntityStorage.loadEntity(chunk.getWorld(), entityTag);
                } catch (Exception e) {
                    GlowServer.logger.log(Level.WARNING, "Error loading entity in " + chunk, e);
                }
            }
        }

        // read "HeightMap" if we need to

        // read tile entities
        List<CompoundTag> storedTileEntities = levelTag.getCompoundList("TileEntities");
        for (CompoundTag tileEntityTag : storedTileEntities) {
            int tx = tileEntityTag.getInt("x");
            int ty = tileEntityTag.getInt("y");
            int tz = tileEntityTag.getInt("z");
            TileEntity tileEntity = chunk.getEntity(tx & 0xf, ty, tz & 0xf);
            if (tileEntity != null) {
                try {
                    tileEntity.loadNbt(tileEntityTag);
                } catch (Exception ex) {
                    GlowServer.logger.log(Level.SEVERE, "Error loading TileEntity at " + tileEntity.getBlock(), ex);
                }
            } else {
                GlowServer.logger.warning("No tile entity at " + chunk.getWorld() + "," + tx + "," + ty + "," + tz);
            }
        }

        return true;
    }

    /**
     * Writes a chunk to its region file.
     * @param chunk The {@link GlowChunk} to write from.
     * @throws IOException if an I/O error occurs.
     */
    @Override
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

            byte[] rawTypes = new byte[sec.types.length];
            NibbleArray extTypes = null;
            NibbleArray data = new NibbleArray(sec.types.length);
            for (int j = 0; j < sec.types.length; j++) {
                rawTypes[j] = (byte) ((sec.types[j] >> 4) & 0xFF);
                byte extType = (byte) (sec.types[j] >> 12);
                if (extType > 0) {
                    if (extTypes == null) {
                        extTypes = new NibbleArray(sec.types.length);
                    }
                    extTypes.set(j, extType);
                }
                data.set(j, (byte) (sec.types[j] & 0xF));
            }
            sectionTag.putByteArray("Blocks", rawTypes);
            if (extTypes != null) {
                sectionTag.putByteArray("Add", extTypes.getRawData());
            }
            sectionTag.putByteArray("Data", data.getRawData());
            sectionTag.putByteArray("BlockLight", sec.blockLight.getRawData());
            sectionTag.putByteArray("SkyLight", sec.skyLight.getRawData());

            sectionTags.add(sectionTag);
        }
        levelTags.putCompoundList("Sections", sectionTags);

        // height map and biomes
        levelTags.putIntArray("HeightMap", snapshot.getRawHeightmap());
        levelTags.putByteArray("Biomes", snapshot.getRawBiomes());

        // entities
        List<CompoundTag> entities = new ArrayList<>();
        for (GlowEntity entity : chunk.getRawEntities()) {
            if (!entity.shouldSave()) {
                continue;
            }
            try {
                CompoundTag tag = new CompoundTag();
                EntityStorage.save(entity, tag);
                entities.add(tag);
            } catch (Exception e) {
                GlowServer.logger.log(Level.WARNING, "Error saving " + entity + " in " + chunk, e);
            }
        }
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

    @Override
    public void unload() throws IOException {
        cache.clear();
    }

}
