package net.glowstone.block;

import java.util.ArrayList;
import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.glowstone.block.blocktype.BlockRedstone;
import net.glowstone.block.blocktype.BlockRedstoneTorch;
import org.bukkit.material.Button;
import org.bukkit.material.Diode;
import org.bukkit.material.Lever;

/**
 * Represents a single block in a world.
 */
public final class GlowBlock implements Block {

    /**
     * The BlockFaces of a single-layer 3x3 area.
     */
    private static final BlockFace[] LAYER = new BlockFace[]{
        BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST,
        BlockFace.EAST, BlockFace.SELF, BlockFace.WEST,
        BlockFace.SOUTH_WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST};

    /**
     * The BlockFaces of all directly adjacent.
     */
    private static final BlockFace[] ADJACENT = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    /**
     * The metadata store class for blocks.
     */
    private static final class BlockMetadataStore extends MetadataStoreBase<Block> implements MetadataStore<Block> {

        @Override
        protected String disambiguate(Block subject, String metadataKey) {
            return subject.getWorld() + "," + subject.getX() + "," + subject.getY() + "," + subject.getZ() + ":" + metadataKey;
        }
    }

    /**
     * The metadata store for blocks.
     */
    private static final MetadataStore<Block> metadata = new BlockMetadataStore();

    private final GlowChunk chunk;
    private final int x;
    private final int y;
    private final int z;

    public GlowBlock(GlowChunk chunk, int x, int y, int z) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basics
    @Override
    public GlowWorld getWorld() {
        return chunk.getWorld();
    }

    @Override
    public GlowChunk getChunk() {
        return chunk;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    @Override
    public Location getLocation(Location loc) {
        if (loc == null) {
            return null;
        }
        loc.setWorld(getWorld());
        loc.setX(x);
        loc.setY(y);
        loc.setZ(z);
        return loc;
    }

    public TileEntity getTileEntity() {
        return chunk.getEntity(x & 0xf, y, z & 0xf);
    }

    @Override
    public GlowBlockState getState() {
        TileEntity entity = getTileEntity();
        if (entity != null) {
            GlowBlockState state = entity.getState();
            if (state != null) {
                return state;
            }
        }
        return new GlowBlockState(this);
    }

    @Override
    public Biome getBiome() {
        return getWorld().getBiome(x, z);
    }

    @Override
    public void setBiome(Biome bio) {
        getWorld().setBiome(x, z, bio);
    }

    @Override
    public double getTemperature() {
        return getWorld().getTemperature(x, z);
    }

    @Override
    public double getHumidity() {
        return getWorld().getHumidity(x, z);
    }

    ////////////////////////////////////////////////////////////////////////////
    // getFace & getRelative
    @Override
    public BlockFace getFace(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if ((x + face.getModX() == block.getX())
                    && (y + face.getModY() == block.getY())
                    && (z + face.getModZ() == block.getZ())) {
                return face;
            }
        }
        return null;
    }

    @Override
    public GlowBlock getRelative(int modX, int modY, int modZ) {
        return getWorld().getBlockAt(x + modX, y + modY, z + modZ);
    }

    @Override
    public GlowBlock getRelative(BlockFace face) {
        return getRelative(face.getModX(), face.getModY(), face.getModZ());
    }

    @Override
    public GlowBlock getRelative(BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Type and typeid getters/setters
    @Override
    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    @Override
    public int getTypeId() {
        return chunk.getType(x & 0xf, z & 0xf, y);
    }

    @Override
    public void setType(Material type) {
        setTypeId(type.getId());
    }

    /**
     * Set the Material type of a block and optionally apply physics.
     */
    @Override
    public void setType(Material type, boolean applyPhysics) {
        setTypeId(type.getId(), applyPhysics);
    }

    /**
     * Set the Material type of a block with metadata? and optionally apply
     * physics.
     */
    public void setType(Material type, byte data, boolean applyPhysics) {
        setTypeIdAndData(type.getId(), data, applyPhysics);
    }

    @Override
    public boolean setTypeId(int type) {
        return setTypeId(type, true);
    }

    @Override
    public boolean setTypeId(int type, boolean applyPhysics) {
        return setTypeIdAndData(type, (byte) 0, applyPhysics);
    }

    @Override
    public boolean setTypeIdAndData(int type, byte data, boolean applyPhysics) {
        Material oldTypeId = getType();
        byte oldData = getData();

        chunk.setType(x & 0xf, z & 0xf, y, type);
        chunk.setMetaData(x & 0xf, z & 0xf, y, data);

        if (oldTypeId == Material.DOUBLE_PLANT && this.getRelative(BlockFace.UP).getType() == Material.DOUBLE_PLANT) {
            chunk.setType(x & 0xf, z & 0xf, y + 1, 0);
            chunk.setMetaData(x & 0xf, z & 0xf, y, 0);
            BlockChangeMessage bcmsg = new BlockChangeMessage(x, y + 1, z, 0, 0);
            for (GlowPlayer p : getWorld().getRawPlayers()) {
                p.sendBlockChange(bcmsg);
            }
        }

        if (applyPhysics) {
            applyPhysics(oldTypeId, type, oldData, data);
        }

        BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, type, data);
        for (GlowPlayer p : getWorld().getRawPlayers()) {
            p.sendBlockChange(bcmsg);
        }

        return true;
    }

    @Override
    public boolean isEmpty() {
        return getTypeId() == 0;
    }

    @Override
    public boolean isLiquid() {
        Material mat = getType();
        return mat == Material.WATER || mat == Material.STATIONARY_WATER || mat == Material.LAVA || mat == Material.STATIONARY_LAVA;
    }

    /**
     * Get block material's flammable ability.
     */
    public boolean isFlammable() {
        return getMaterialValues().getFlameResistance() >= 0;
    }

    /**
     * Get block material's burn ability.
     */
    public boolean isBurnable() {
        return getMaterialValues().getFireResistance() >= 0;
    }

    public MaterialValueManager.ValueCollection getMaterialValues() {
        return ((GlowServer) Bukkit.getServer()).getMaterialValueManager().getValues(getType());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Data and light getters/setters
    @Override
    public byte getData() {
        return (byte) chunk.getMetaData(x & 0xf, z & 0xf, y);
    }

    @Override
    public void setData(byte data) {
        setData(data, true);
    }

    @Override
    public void setData(byte data, boolean applyPhysics) {
        byte oldData = getData();
        chunk.setMetaData(x & 0xf, z & 0xf, y & 0x7f, data);
        if (applyPhysics) {
            applyPhysics(getType(), getTypeId(), oldData, data);
        }
        BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, getTypeId(), data);
        for (GlowPlayer p : getWorld().getRawPlayers()) {
            p.sendBlockChange(bcmsg);
        }
    }

    @Override
    public byte getLightLevel() {
        return (byte) Math.max(getLightFromSky(), getLightFromBlocks());
    }

    @Override
    public byte getLightFromSky() {
        return chunk.getSkyLight(x & 0xf, z & 0xf, y);
    }

    @Override
    public byte getLightFromBlocks() {
        return chunk.getBlockLight(x & 0xf, z & 0xf, y);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Redstone
    @Override
    public boolean isBlockPowered() {
        // Strong powered?

        if (getType() == Material.REDSTONE_BLOCK) {
            return true;
        }

        if (getType() == Material.LEVER && ((Lever) getState().getData()).isPowered()) {
            return true;
        }

        if ((getType() == Material.WOOD_BUTTON || getType() == Material.STONE_BUTTON)
                && ((Button) getState().getData()).isPowered()) {
            return true;
        }

        // Now checking for power attached, only solid blocks transmit this..
        if (!getType().isSolid()) {
            return false;
        }

        for (BlockFace face : ADJACENT) {
            GlowBlock target = getRelative(face);
            switch (target.getType()) {
                case LEVER:
                    Lever lever = (Lever) target.getState().getData();
                    if (lever.isPowered() && lever.getAttachedFace() == target.getFace(this)) {
                        return true;
                    }
                    break;
                case STONE_BUTTON:
                case WOOD_BUTTON:
                    Button button = (Button) target.getState().getData();
                    if (button.isPowered() && button.getAttachedFace() == target.getFace(this)) {
                        return true;
                    }
                    break;
                case DIODE_BLOCK_ON:
                    if (((Diode) target.getState().getData()).getFacing() == target.getFace(this)) {
                        return true;
                    }
                    break;
                case REDSTONE_TORCH_ON:
                    if (face == BlockFace.DOWN) {
                        return true;
                    }
                    break;
                case REDSTONE_WIRE:
                    if (target.getData() > 0 && BlockRedstone.calculateConnections(target).contains(target.getFace(this))) {
                        return true;
                    }
                    break;
            }
        }

        return false;
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        // Is a nearby block directly powered?
        for (BlockFace face : ADJACENT) {
            GlowBlock block = getRelative(face);
            if (block.isBlockPowered()) {
                return true;
            }

            switch (block.getType()) {
                case REDSTONE_TORCH_ON:
                    if (face != BlockRedstoneTorch.getAttachedBlockFace(block).getOppositeFace()) {
                        return true;
                    }
                    break;
                case REDSTONE_WIRE:
                    if (block.getData() > 0 && BlockRedstone.calculateConnections(block).contains(block.getFace(this))) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean isBlockFacePowered(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBlockPower(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBlockPower() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "GlowBlock{chunk=" + getChunk() + ",x=" + x + ",y=" + y + ",z=" + z + ",type=" + getType() + ",data=" + getData() + "}";
    }

    ////////////////////////////////////////////////////////////////////////////
    // Drops and breaking
    /**
     * Break the block naturally, randomly dropping only some of the drops.
     *
     * @param yield The approximate portion of the drops to actually drop.
     * @return true if the block was destroyed
     */
    public boolean breakNaturally(float yield) {
        Random r = new Random();

        if (getType() == Material.AIR) {
            return false;
        }

        Location location = getLocation();
        Collection<ItemStack> toDrop = ItemTable.instance().getBlock(getType()).getMinedDrops(this);
        for (ItemStack stack : toDrop) {
            if (r.nextFloat() < yield) {
                getWorld().dropItemNaturally(location, stack);
            }
        }

        setType(Material.AIR);
        return true;
    }

    @Override
    public boolean breakNaturally() {
        return breakNaturally(1.0f);
    }

    @Override
    public boolean breakNaturally(ItemStack tool) {
        if (!getDrops(tool).isEmpty()) {
            return breakNaturally();
        } else {
            return setTypeId(Material.AIR.getId());
        }
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return ItemTable.instance().getBlock(getType()).getMinedDrops(this);
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack tool) {
        return ItemTable.instance().getBlock(getType()).getDrops(this, tool);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata
    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        metadata.setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return metadata.getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return metadata.hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        metadata.removeMetadata(this, metadataKey, owningPlugin);
    }

    /////////////////////////////////////////////////////////////////////////////
    // Physics
    /**
     * Notify this block and its surrounding blocks that this block has changed
     * type and data.
     *
     * @param oldType the old block type
     * @param newTypeId the new block type
     * @param oldData the old data
     * @param newData the new data
     */
    public void applyPhysics(Material oldType, int newTypeId, byte oldData, byte newData) {
        // notify the surrounding blocks that this block has changed
        ItemTable itemTable = ItemTable.instance();
        Material newType = Material.getMaterial(newTypeId);

        for (int y = -1; y <= 1; y++) {
            for (BlockFace face : LAYER) {
                if (y == 0 && face == BlockFace.SELF) {
                    continue;
                }

                GlowBlock notify = this.getRelative(face.getModX(), face.getModY() + y, face.getModZ());

                BlockFace blockFace;
                if (y == 0) {
                    blockFace = face.getOppositeFace();
                } else if (y == -1 && face == BlockFace.SELF) {
                    blockFace = BlockFace.UP;
                } else if (y == 1 && face == BlockFace.SELF) {
                    blockFace = BlockFace.DOWN;
                } else {
                    blockFace = null;
                }

                BlockType notifyType = itemTable.getBlock(notify.getTypeId());
                if (notifyType != null) {
                    notifyType.onNearBlockChanged(notify, blockFace, this, oldType, oldData, newType, newData);
                }
            }
        }

        BlockType type = itemTable.getBlock(oldType);
        if (type != null) {
            type.onBlockChanged(this, oldType, oldData, newType, newData);
        }
    }

    private static final Map<GlowBlock, List<Long>> counterMap = new HashMap<>();

    public void count(int timeout) {
        GlowBlock target = this;
        List<Long> gameTicks = new ArrayList<>();
        for (GlowBlock block : counterMap.keySet()) {
            if (block.getLocation().equals(this.getLocation())) {
                gameTicks = counterMap.get(block);
                target = block;
                break;
            }
        }

        long time = getWorld().getFullTime();
        gameTicks.add(time + timeout);

        counterMap.put(target, gameTicks);
    }

    public int getCounter() {
        GlowBlock target = this;
        List<Long> gameTicks = new ArrayList<>();
        for (GlowBlock block : counterMap.keySet()) {
            if (block.getLocation().equals(this.getLocation())) {
                gameTicks = counterMap.get(block);
                target = block;
                break;
            }
        }

        long time = getWorld().getFullTime();

        for (Iterator<Long> it = gameTicks.iterator(); it.hasNext();) {
            long rate = it.next();
            if (rate < time) {
                it.remove();
            }
        }

        counterMap.put(target, gameTicks);
        return gameTicks.size();
    }
}
