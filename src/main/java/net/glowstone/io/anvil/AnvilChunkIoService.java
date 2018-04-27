package net.glowstone.io.anvil;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.chunk.ChunkSection;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.chunk.GlowChunkSnapshot;
import net.glowstone.constants.ItemIds;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.ChunkIoService;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NbtInputStream;
import net.glowstone.util.nbt.NbtOutputStream;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes Anvil maps, an improvement
 * on the McRegion file format.
 */
public final class AnvilChunkIoService implements ChunkIoService {

    /**
     * The size of a region - a 32x32 group of chunks.
     */
    private static final int REGION_SIZE = 32;

    /**
     * The region file cache.
     */
    private final RegionFileCache cache;

    // todo: consider the session.lock file

    public AnvilChunkIoService(File dir) {
        cache = new RegionFileCache(dir, ".mca");
    }

    @Override
    public boolean read(GlowChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();
        RegionFile region = cache.getRegionFile(x, z);
        int regionX = x & REGION_SIZE - 1;
        int regionZ = z & REGION_SIZE - 1;
        if (!region.hasChunk(regionX, regionZ)) {
            return false;
        }

        DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);

        CompoundTag levelTag;
        try (NbtInputStream nbt = new NbtInputStream(in, false)) {
            CompoundTag root = nbt.readCompound();
            levelTag = root.getCompound("Level");
        }

        // read the vertical sections
        List<CompoundTag> sectionList = levelTag.getCompoundList("Sections");
        ChunkSection[] sections = new ChunkSection[GlowChunk.SEC_COUNT];
        for (CompoundTag sectionTag : sectionList) {
            int y = sectionTag.getByte("Y");
            if (y < 0 || y > GlowChunk.SEC_COUNT) {
                GlowServer.logger.log(Level.WARNING,
                        "Out of bounds chunk section at y " + y + " in " + chunk + "!");
                continue;
            }
            if (sections[y] != null) {
                GlowServer.logger
                    .log(Level.WARNING, "Multiple chunk sections at y " + y + " in " + chunk + "!");
                continue;
            }
            sections[y] = ChunkSection.fromNbt(sectionTag);
        }

        // initialize the chunk
        chunk.initializeSections(sections);
        chunk.setPopulated(levelTag.getBoolDefaultFalse("TerrainPopulated"));

        // read biomes
        levelTag.consumeByteArray(chunk::setBiomes, "Biomes");
        // read height map
        if (!levelTag.consumeIntArray(chunk::setHeightMap, "HeightMap")) {
            chunk.automaticHeightMap();
        }

        // read slime chunk
        levelTag.consumeByte(chunk::setIsSlimeChunk, "isSlimeChunk");

        // read entities
        levelTag.consumeCompoundList(entities -> {
            for (CompoundTag entityTag : entities) {
                try {
                    // note that creating the entity is sufficient to add it to the world
                    EntityStorage.loadEntity(chunk.getWorld(), entityTag);
                } catch (Exception e) {
                    String id = entityTag.isString("id") ? entityTag.getString("id") : "<missing>";
                    if (e.getMessage() != null && e.getMessage()
                        .startsWith("Unknown entity type to load:")) {
                        GlowServer.logger.warning("Unknown entity in " + chunk + ": " + id);
                    } else {
                        GlowServer.logger
                            .log(Level.WARNING, "Error loading entity in " + chunk + ": " + id, e);
                    }
                }
            }
        }, "Entities");

        // read block entities
        List<CompoundTag> storedBlockEntities = levelTag.getCompoundList("TileEntities");
        BlockEntity blockEntity;
        for (CompoundTag blockEntityTag : storedBlockEntities) {
            int tx = blockEntityTag.getInt("x");
            int ty = blockEntityTag.getInt("y");
            int tz = blockEntityTag.getInt("z");
            blockEntity = chunk
                .createEntity(tx & 0xf, ty, tz & 0xf, chunk.getType(tx & 0xf, tz & 0xf, ty));
            if (blockEntity != null) {
                try {
                    blockEntity.loadNbt(blockEntityTag);
                } catch (Exception ex) {
                    String id = blockEntityTag.isString("id") ? blockEntityTag.getString("id")
                        : "<missing>";
                    GlowServer.logger.log(Level.SEVERE,
                        "Error loading block entity at " + blockEntity.getBlock() + ": " + id, ex);
                }
            } else {
                String id =
                    blockEntityTag.isString("id") ? blockEntityTag.getString("id") : "<missing>";
                GlowServer.logger.warning(
                    "Unknown block entity at " + chunk.getWorld().getName() + "," + tx + "," + ty
                        + "," + tz + ": " + id);
            }
        }

        if (levelTag.isList("TileTicks", TagType.COMPOUND)) {
            List<CompoundTag> tileTicks = levelTag.getCompoundList("TileTicks");
            for (CompoundTag tileTick : tileTicks) {
                int tileX = tileTick.getInt("x");
                int tileY = tileTick.getInt("y");
                int tileZ = tileTick.getInt("z");
                String id = tileTick.getString("i");
                Material material = ItemIds.getBlock(id);
                if (material == null) {
                    GlowServer.logger
                        .warning("Unknown block '" + id + "' when loading chunk block ticks.");
                    continue;
                }
                GlowBlock block = chunk.getBlock(tileX, tileY, tileZ);
                if (material != block.getType()) {
                    continue;
                }
                // TODO tick delay: tileTick.getInt("t");
                // TODO ordering: tileTick.getInt("p");
                BlockType type = ItemTable.instance().getBlock(material);
                if (type == null) {
                    continue;
                }
                block.getWorld().requestPulse(block);
            }
        }

        return true;
    }

    @Override
    public void write(GlowChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();
        RegionFile region = cache.getRegionFile(x, z);
        int regionX = x & REGION_SIZE - 1;
        int regionZ = z & REGION_SIZE - 1;

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
            if (sec == null) {
                continue;
            }

            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putByte("Y", i);
            sec.optimize();
            sec.writeToNbt(sectionTag);
            sectionTags.add(sectionTag);
        }
        levelTags.putCompoundList("Sections", sectionTags);

        // height map and biomes
        levelTags.putIntArray("HeightMap", snapshot.getRawHeightmap());
        levelTags.putByteArray("Biomes", snapshot.getRawBiomes());

        // Save Slime Chunk
        levelTags.putByte("isSlimeChunk", snapshot.isSlimeChunk() ? 1 : 0);

        // entities
        List<CompoundTag> entities = new ArrayList<>();
        for (GlowEntity entity : chunk.getRawEntities()) {
            if (!entity.shouldSave()) {
                continue;
            }
            // passengers will be saved as part of the vehicle
            if (entity.isInsideVehicle()) {
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

        // block entities
        List<CompoundTag> blockEntities = new ArrayList<>();
        for (BlockEntity entity : chunk.getRawBlockEntities()) {
            try {
                CompoundTag tag = new CompoundTag();
                entity.saveNbt(tag);
                blockEntities.add(tag);
            } catch (Exception ex) {
                GlowServer.logger
                    .log(Level.SEVERE, "Error saving block entity at " + entity.getBlock(), ex);
            }
        }
        levelTags.putCompoundList("TileEntities", blockEntities);

        List<CompoundTag> tileTicks = new ArrayList<>();
        for (Location location : chunk.getWorld().getTickMap()) {
            Chunk locationChunk = location.getChunk();
            if (locationChunk.getX() == chunk.getX() && locationChunk.getZ() == chunk.getZ()) {
                int tileX = location.getBlockX();
                int tileY = location.getBlockY();
                int tileZ = location.getBlockZ();
                String type = ItemIds.getName(location.getBlock().getType());
                CompoundTag tag = new CompoundTag();
                tag.putInt("x", tileX);
                tag.putInt("y", tileY);
                tag.putInt("z", tileZ);
                tag.putString("i", type);
                tileTicks.add(tag);
            }
        }
        levelTags.putCompoundList("TileTicks", tileTicks);

        CompoundTag levelOut = new CompoundTag();
        levelOut.putCompound("Level", levelTags);

        try (NbtOutputStream nbt = new NbtOutputStream(
            region.getChunkDataOutputStream(regionX, regionZ), false)) {
            nbt.writeTag(levelOut);
        }
    }

    @Override
    public void unload() throws IOException {
        cache.clear();
    }

}
