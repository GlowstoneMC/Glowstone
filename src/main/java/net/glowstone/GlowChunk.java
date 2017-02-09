package net.glowstone;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.game.ChunkDataMessage;
import net.glowstone.util.NibbleArray;
import net.glowstone.util.VariableValueArray;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Chunk;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.*;
import java.util.logging.Level;

/**
 * Represents a chunk of the map.
 *
 * @author Graham Edgecombe
 */
public final class GlowChunk implements Chunk {

    /**
     * The dimensions of a chunk (width: x, height: z, depth: y).
     */
    public static final int WIDTH = 16, HEIGHT = 16, DEPTH = 256;
    /**
     * The Y depth of a single chunk section.
     */
    private static final int SEC_DEPTH = 16;
    /**
     * The world of this chunk.
     */
    private final GlowWorld world;
    /**
     * The coordinates of this chunk.
     */
    private final int x, z;
    /**
     * The tile entities that reside in this chunk.
     */
    private final HashMap<Integer, TileEntity> tileEntities = new HashMap<>();
    /**
     * The entities that reside in this chunk.
     */
    private final Set<GlowEntity> entities = new HashSet<>(4);
    /**
     * The array of chunk sections this chunk contains, or null if it is unloaded.
     */
    private ChunkSection[] sections;

    /**
     * The array of biomes this chunk contains, or null if it is unloaded.
     */
    private byte[] biomes;

    /**
     * The height map values values of each column, or null if it is unloaded.
     * The height for a column is one plus the y-index of the highest non-air
     * block in the column.
     */
    private byte[] heightMap;
    /**
     * Whether the chunk has been populated by special features.
     * Used in map generation.
     */
    private boolean populated;

    /**
     * Creates a new chunk with a specified X and Z coordinate.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    GlowChunk(GlowWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return "GlowChunk{world=" + world.getName() + ",x=" + x + ",z=" + z + '}';
    }

    @Override
    public GlowWorld getWorld() {
        return world;
    }

    @Override
    public int getX() {
        return x;
    }

    // ======== Basic stuff ========

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public GlowBlock getBlock(int x, int y, int z) {
        return new GlowBlock(this, this.x << 4 | x & 0xf, y & 0xff, this.z << 4 | z & 0xf);
    }

    @Override
    public Entity[] getEntities() {
        return entities.toArray(new Entity[entities.size()]);
    }

    public Collection<GlowEntity> getRawEntities() {
        return entities;
    }

    @Override
    public GlowBlockState[] getTileEntities() {
        List<GlowBlockState> states = new ArrayList<>(tileEntities.size());
        for (TileEntity tileEntity : tileEntities.values()) {
            GlowBlockState state = tileEntity.getState();
            if (state != null) {
                states.add(state);
            }
        }

        return states.toArray(new GlowBlockState[states.size()]);
    }

    public Collection<TileEntity> getRawTileEntities() {
        return Collections.unmodifiableCollection(tileEntities.values());
    }

    @Override
    public GlowChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    @Override
    public GlowChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
        return new GlowChunkSnapshot(x, z, world, sections,
                includeMaxBlockY ? heightMap.clone() : null,
                includeBiome ? biomes.clone() : null,
                includeBiomeTempRain);
    }

    /**
     * Gets whether this chunk has been populated by special features.
     *
     * @return Population status.
     */
    public boolean isPopulated() {
        return populated;
    }

    /**
     * Sets the population status of this chunk.
     *
     * @param populated Population status.
     */
    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    @Override
    public boolean isLoaded() {
        return sections != null;
    }

    @Override
    public boolean load() {
        return load(true);
    }

    // ======== Helper Functions ========

    @Override
    public boolean load(boolean generate) {
        return isLoaded() || world.getChunkManager().loadChunk(x, z, generate);
    }

    @Override
    public boolean unload() {
        return unload(true, true);
    }

    @Override
    public boolean unload(boolean save) {
        return unload(save, true);
    }

    @Override
    public boolean unload(boolean save, boolean safe) {
        if (!isLoaded()) {
            return true;
        }

        if (safe && world.isChunkInUse(x, z)) {
            return false;
        }

        if (save && !world.getChunkManager().performSave(this)) {
            return false;
        }

        if (EventFactory.callEvent(new ChunkUnloadEvent(this)).isCancelled()) {
            return false;
        }

        sections = null;
        biomes = null;
        heightMap = null;
        tileEntities.clear();
        if (save) {
            for (GlowEntity entity : entities) {
                entity.remove();
            }
            entities.clear();
        }
        return true;
    }

    /**
     * Initialize this chunk from the given sections.
     *
     * @param initSections The ChunkSections to use.
     */
    public void initializeSections(ChunkSection... initSections) {
        if (isLoaded()) {
            GlowServer.logger.log(Level.SEVERE, "Tried to initialize already loaded chunk (" + x + "," + z + ")", new Throwable());
            return;
        }
        //GlowServer.logger.log(Level.INFO, "Initializing chunk ({0},{1})", new Object[]{x, z});

        sections = new ChunkSection[DEPTH / SEC_DEPTH];
        System.arraycopy(initSections, 0, sections, 0, Math.min(sections.length, initSections.length));

        biomes = new byte[WIDTH * HEIGHT];
        heightMap = new byte[WIDTH * HEIGHT];

        // tile entity initialization
        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] == null) continue;
            int by = 16 * i;
            for (int cx = 0; cx < WIDTH; ++cx) {
                for (int cz = 0; cz < HEIGHT; ++cz) {
                    for (int cy = by; cy < by + 16; ++cy) {
                        createEntity(cx, cy, cz, getType(cx, cz, cy));
                    }
                }
            }
        }
    }

    /**
     * If needed, create a new tile entity at the given location.
     */
    private void createEntity(int cx, int cy, int cz, int type) {
        BlockType blockType = ItemTable.instance().getBlock(type);
        if (blockType == null) return;

        try {
            TileEntity entity = blockType.createTileEntity(this, cx, cy, cz);
            if (entity == null) return;

            tileEntities.put(coordinateToIndex(cx, cz, cy), entity);
        } catch (Exception ex) {
            GlowServer.logger.log(Level.SEVERE, "Unable to initialize tile entity for " + type, ex);
        }
    }

    /**
     * Attempt to get the ChunkSection at the specified height.
     *
     * @param y the y value.
     * @return The ChunkSection, or null if it is empty.
     */
    private ChunkSection getSection(int y) {
        int idx = y >> 4;
        if (y < 0 || y >= DEPTH || !load() || idx >= sections.length) {
            return null;
        }
        return sections[idx];
    }

    /**
     * Get all ChunkSection of this chunk.
     *
     * @return The chunk sections array.
     */
    public ChunkSection[] getSections() {
        return sections;
    }

    // ======== Data access ========

    /**
     * Attempt to get the tile entity located at the given coordinates.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return A GlowBlockState if the entity exists, or null otherwise.
     */
    public TileEntity getEntity(int x, int y, int z) {
        if (y >= DEPTH || y < 0) return null;
        load();
        return tileEntities.get(coordinateToIndex(x, z, y));
    }

    /**
     * Gets the type of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The type.
     */
    public int getType(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.types[section.index(x, y, z)] >> 4;
    }

    /**
     * Sets the type of a block within this chunk.
     *
     * @param x    The X coordinate.
     * @param z    The Z coordinate.
     * @param y    The Y coordinate.
     * @param type The type.
     */
    public void setType(int x, int z, int y, int type) {
        if (type < 0 || type > 0xfff)
            throw new IllegalArgumentException("Block type out of range: " + type);

        ChunkSection section = getSection(y);
        if (section == null) {
            if (type == 0) {
                // don't need to create chunk for air
                return;
            } else {
                // create new ChunkSection for this y coordinate
                int idx = y >> 4;
                if (y < 0 || y >= DEPTH || idx >= sections.length) {
                    // y is out of range somehow
                    return;
                }
                sections[idx] = section = new ChunkSection();
            }
        }

        // destroy any tile entity there
        int tileEntityIndex = coordinateToIndex(x, z, y);
        if (tileEntities.containsKey(tileEntityIndex)) {
            tileEntities.remove(tileEntityIndex).destroy();
        }

        // update the air count and height map
        int index = section.index(x, y, z);
        int heightIndex = z * WIDTH + x;
        if (type == 0) {
            if (section.types[index] != 0) {
                section.count--;
            }
            if (heightMap[heightIndex] == y + 1) {
                // erased just below old height map -> lower
                heightMap[heightIndex] = (byte) lowerHeightMap(x, y, z);
            }
        } else {
            if (section.types[index] == 0) {
                section.count++;
            }
            if (heightMap[heightIndex] <= y) {
                // placed between old height map and top -> raise
                heightMap[heightIndex] = (byte) Math.min(y + 1, 255);
            }
        }
        // update the type - also sets metadata to 0
        section.types[index] = (char) (type << 4);

        if (type == 0 && section.count == 0) {
            // destroy the empty section
            sections[y / SEC_DEPTH] = null;
            return;
        }

        // create a new tile entity if we need
        createEntity(x, y, z, type);
    }

    /**
     * Scan downwards to determine the new height map value.
     */
    private int lowerHeightMap(int x, int y, int z) {
        for (--y; y >= 0; --y) {
            if (getType(x, z, y) != 0) {
                break;
            }
        }
        return y + 1;
    }

    /**
     * Gets the metadata of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The metadata.
     */
    public int getMetaData(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.types[section.index(x, y, z)] & 0xF;
    }

    /**
     * Sets the metadata of a block within this chunk.
     *
     * @param x        The X coordinate.
     * @param z        The Z coordinate.
     * @param y        The Y coordinate.
     * @param metaData The metadata.
     */
    public void setMetaData(int x, int z, int y, int metaData) {
        if (metaData < 0 || metaData >= 16)
            throw new IllegalArgumentException("Metadata out of range: " + metaData);
        ChunkSection section = getSection(y);
        if (section == null) return;  // can't set metadata on an empty section
        int index = section.index(x, y, z);
        int type = section.types[index];
        if (type == 0) return;  // can't set metadata on air
        section.types[index] = (char) (type & 0xfff0 | metaData);
    }

    /**
     * Gets the sky light level of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The sky light level.
     */
    public byte getSkyLight(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.skyLight.get(section.index(x, y, z));
    }

    /**
     * Sets the sky light level of a block within this chunk.
     *
     * @param x        The X coordinate.
     * @param z        The Z coordinate.
     * @param y        The Y coordinate.
     * @param skyLight The sky light level.
     */
    public void setSkyLight(int x, int z, int y, int skyLight) {
        ChunkSection section = getSection(y);
        if (section == null) return;  // can't set light on an empty section
        section.skyLight.set(section.index(x, y, z), (byte) skyLight);
    }

    /**
     * Gets the block light level of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The block light level.
     */
    public byte getBlockLight(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.blockLight.get(section.index(x, y, z));
    }

    /**
     * Sets the block light level of a block within this chunk.
     *
     * @param x          The X coordinate.
     * @param z          The Z coordinate.
     * @param y          The Y coordinate.
     * @param blockLight The block light level.
     */
    public void setBlockLight(int x, int z, int y, int blockLight) {
        ChunkSection section = getSection(y);
        if (section == null) return;  // can't set light on an empty section
        section.blockLight.set(section.index(x, y, z), (byte) blockLight);
    }

    /**
     * Gets the biome of a column within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The biome.
     */
    public int getBiome(int x, int z) {
        if (biomes == null && !load()) return 0;
        return biomes[z * WIDTH + x] & 0xFF;
    }

    /**
     * Sets the biome of a column within this chunk,
     *
     * @param x     The X coordinate.
     * @param z     The Z coordinate.
     * @param biome The biome.
     */
    public void setBiome(int x, int z, int biome) {
        if (biomes == null) return;
        biomes[z * WIDTH + x] = (byte) biome;
    }

    /**
     * Set the entire biome array of this chunk.
     *
     * @param newBiomes The biome array.
     */
    public void setBiomes(byte... newBiomes) {
        if (biomes == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newBiomes.length != biomes.length) {
            throw new IllegalArgumentException("Biomes array not of length " + biomes.length);
        }
        System.arraycopy(newBiomes, 0, biomes, 0, biomes.length);
    }

    /**
     * Get the height map value of a column within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The height map value.
     */
    public int getHeight(int x, int z) {
        if (heightMap == null && !load()) return 0;
        return heightMap[z * WIDTH + x] & 0xff;
    }

    /**
     * Set the entire height map of this chunk.
     *
     * @param newHeightMap The height map.
     */
    public void setHeightMap(int... newHeightMap) {
        if (heightMap == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newHeightMap.length != heightMap.length) {
            throw new IllegalArgumentException("Height map not of length " + heightMap.length);
        }
        for (int i = 0; i < heightMap.length; ++i) {
            heightMap[i] = (byte) newHeightMap[i];
        }
    }

    /**
     * Automatically fill the height map after chunks have been initialized.
     */
    public void automaticHeightMap() {
        // determine max Y chunk section at a time
        int sy = sections.length - 1;
        for (; sy >= 0; --sy) {
            if (sections[sy] != null) {
                break;
            }
        }
        int y = (sy + 1) * 16;
        for (int x = 0; x < WIDTH; ++x) {
            for (int z = 0; z < HEIGHT; ++z) {
                heightMap[z * WIDTH + x] = (byte) lowerHeightMap(x, y, z);
            }
        }
    }

    /**
     * Converts a three-dimensional coordinate to an index within the
     * one-dimensional arrays.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The index within the arrays.
     */
    private int coordinateToIndex(int x, int z, int y) {
        if (x < 0 || z < 0 || y < 0 || x >= WIDTH || z >= HEIGHT || y >= DEPTH)
            throw new IndexOutOfBoundsException("Coords (x=" + x + ",y=" + y + ",z=" + z + ") invalid");

        return (y * HEIGHT + z) * WIDTH + x;
    }

    /**
     * Creates a new {@link ChunkDataMessage} which can be sent to a client to stream
     * this entire chunk to them.
     *
     * @return The {@link ChunkDataMessage}.
     */
    public ChunkDataMessage toMessage() {
        // this may need to be changed to "true" depending on resolution of
        // some inconsistencies on the wiki
        return toMessage(world.getEnvironment() == Environment.NORMAL);
    }

    // ======== Helper functions ========

    /**
     * Creates a new {@link ChunkDataMessage} which can be sent to a client to stream
     * this entire chunk to them.
     *
     * @param skylight Whether to include skylight data.
     * @return The {@link ChunkDataMessage}.
     */
    public ChunkDataMessage toMessage(boolean skylight) {
        return toMessage(skylight, true);
    }

    private static final int PACKET_DATA_ARRAY_LENGTH = ChunkSection.ARRAY_SIZE * 2 / 8;
    private static final byte[] PACKET_DATA_ARRAY_LENGTH_BYTES;

    static {
        ByteBuf buf = Unpooled.buffer();
        try {
            ByteBufUtils.writeVarInt(buf, PACKET_DATA_ARRAY_LENGTH);
            PACKET_DATA_ARRAY_LENGTH_BYTES = buf.array().clone();
        } finally {
            buf.release();
        }
    }

    /**
     * Creates a new {@link ChunkDataMessage} which can be sent to a client to stream
     * parts of this chunk to them.
     *
     * @param skylight Whether to include skylight data.
     * @param entireChunk Whether to send all chunk sections.
     * @return The {@link ChunkDataMessage}.
     */
    public ChunkDataMessage toMessage(boolean skylight, boolean entireChunk) {
        load();
        int sectionBitmask = 0;

        // filter sectionBitmask based on actual chunk contents
        if (sections != null) {
            int maxBitmask = (1 << sections.length) - 1;
            if (entireChunk) {
                sectionBitmask = maxBitmask;
            } else {
                sectionBitmask &= maxBitmask;
            }

            for (int i = 0; i < sections.length; ++i) {
                if (sections[i] == null || sections[i].count == 0) {
                    // remove empty sections from bitmask
                    sectionBitmask &= ~(1 << i);
                }
            }
        }

        ByteBuf buf = Unpooled.buffer();

        if (sections != null) {
            // get the list of sections
            for (int i = 0; i < sections.length; ++i) {
                if ((sectionBitmask & 1 << i) == 0) {
                    continue;
                }
                ChunkSection section = sections[i];
                VariableValueArray array = new VariableValueArray(13, section.types.length);
                buf.writeByte(array.getBitsPerValue()); // Bit per value -> Currently fixed at 13!!!
                ByteBufUtils.writeVarInt(buf, 0); // Palette size -> 0 -> Use the global palette
                for (int j = 0; j < section.types.length; j++) {
                    array.set(j, section.types[j]);
                }
                long[] backing = array.getBacking();
                ByteBufUtils.writeVarInt(buf, backing.length);
                buf.ensureWritable(backing.length * 8 + section.blockLight.byteSize() + (skylight ? section.skyLight.byteSize() : 0));
                for (long value : backing) {
                    buf.writeLong(value);
                }
                buf.writeBytes(section.blockLight.getRawData());
                if (skylight) {
                    buf.writeBytes(section.skyLight.getRawData());
                }
            }
        }

        // biomes
        if (entireChunk && biomes != null) {
            buf.writeBytes(biomes);
        }

        ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (TileEntity tileEntity : getRawTileEntities()) {
            CompoundTag tag = new CompoundTag();
            tileEntity.saveNbt(tag);
            tiles.add(tag);
        }

        return new ChunkDataMessage(x, z, entireChunk, sectionBitmask, buf, tiles.toArray(new CompoundTag[tiles.size()]));
    }

    /**
     * A chunk key represents the X and Z coordinates of a chunk in a manner
     * suitable for use as a key in a hash table or set.
     */
    @Data
    public static final class Key {
        /**
         * The coordinates.
         */
        private final int x, z;
    }

    /**
     * A single cubic section of a chunk, with all data.
     */
    public static final class ChunkSection {
        private static final int ARRAY_SIZE = WIDTH * HEIGHT * SEC_DEPTH;

        // these probably should be made non-public
        public final char[] types;
        public final NibbleArray skyLight;
        public final NibbleArray blockLight;
        public int count; // amount of non-air blocks

        /**
         * Create a new, empty ChunkSection.
         */
        public ChunkSection() {
            types = new char[ARRAY_SIZE];
            skyLight = new NibbleArray(ARRAY_SIZE);
            blockLight = new NibbleArray(ARRAY_SIZE);
            skyLight.fill((byte) 0xf);
        }

        /**
         * Create a ChunkSection with the specified chunk data. This
         * ChunkSection assumes ownership of the arrays passed in, and they
         * should not be further modified.
         *
         * @param types An array of block types for this chunk section.
         * @param skyLight An array for skylight data for this chunk section.
         * @param blockLight An array for blocklight data for this chunk section.
         */
        public ChunkSection(char[] types, NibbleArray skyLight, NibbleArray blockLight) {
            if (types.length != ARRAY_SIZE || skyLight.size() != ARRAY_SIZE || blockLight.size() != ARRAY_SIZE) {
                throw new IllegalArgumentException("An array length was not " + ARRAY_SIZE + ": " + types.length + " " + skyLight.size() + " " + blockLight.size());
            }
            this.types = types;
            this.skyLight = skyLight;
            this.blockLight = blockLight;
            recount();
        }

        /**
         * Calculate the index into internal arrays for the given coordinates.
         *
         * @param x The x coordinate, for east and west.
         * @param y The y coordinate, for up and down.
         * @param z The z coordinate, for north and south.
         *
         * @return The index.
         */
        public int index(int x, int y, int z) {
            if (x < 0 || z < 0 || x >= WIDTH || z >= HEIGHT) {
                throw new IndexOutOfBoundsException("Coords (x=" + x + ",z=" + z + ") out of section bounds");
            }
            return (y & 0xf) << 8 | z << 4 | x;
        }

        /**
         * Recount the amount of non-air blocks in the chunk section.
         */
        public void recount() {
            count = 0;
            for (char type : types) {
                if (type != 0) {
                    count++;
                }
            }
        }

        /**
         * Take a snapshot of this section which will not reflect future changes.
         *
         * @return The snapshot for this section.
         */
        public ChunkSection snapshot() {
            return new ChunkSection(types.clone(), skyLight.snapshot(), blockLight.snapshot());
        }
    }

}
