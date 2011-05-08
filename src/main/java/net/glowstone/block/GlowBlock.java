package net.glowstone.block;

import net.glowstone.GlowChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

/**
 *
 * @author Tad
 */
public class GlowBlock implements Block {

    private GlowChunk chunk;
    private int x;
    private int y;
    private int z;

    public GlowBlock(GlowChunk chunk, int x, int y, int z) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Basic getters

    public World getWorld() {
        return chunk.getWorld();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public BlockState getState() {
        return new GlowBlockState(this);
    }

    public Biome getBiome() {
        return Biome.PLAINS;
    }

    // getFace & getRelative

    public BlockFace getFace(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if (    (x + face.getModX() == block.getX()) &&
                    (y + face.getModY() == block.getY()) &&
                    (z + face.getModZ() == block.getZ())    ) {
                return face;
            }
        }
        return null;
    }

    public Block getFace(BlockFace face) {
        return getRelative(face.getModX(), face.getModY(), face.getModZ());
    }

    public Block getFace(BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    public Block getRelative(int modX, int modY, int modZ) {
        return getWorld().getBlockAt(x + modX, y + modY, z + modZ);
    }

    public Block getRelative(BlockFace face) {
        return getRelative(face.getModX(), face.getModY(), face.getModZ());
    }
    
    // type and typeid getters/setters

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    public int getTypeId() {
        return chunk.getType(x & 0xf, z & 0xf, y & 0x7f);
    }

    public void setType(Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(int type) {
        return setTypeId(type, true);
    }

    public boolean setTypeId(int type, boolean applyPhysics) {
        // TODO: there aren't actually physics yet or anything else that should prevent setType
        chunk.setType(x & 0xf, z & 0xf, y & 0x7f, type);
        return true;
    }

    public boolean setTypeIdAndData(int type, byte data, boolean applyPhysics) {
        if (setTypeId(type, applyPhysics)) {
            setData(data, applyPhysics);
            return true;
        } else {
            return false;
        }
    }

    // data and light getters/setters

    public byte getData() {
        return (byte) chunk.getMetaData(x & 0xf, z & 0xf, y & 0x7f);
    }

    public void setData(byte data) {
        setData(data, true);
    }

    public void setData(byte data, boolean applyPhyiscs) {
        chunk.setMetaData(x & 0xf, z & 0xf, y & 0x7f, data);
    }

    public byte getLightLevel() {
        return (byte) Math.max(chunk.getSkyLight(x & 0xf, z & 0xf, y & 0x7f), chunk.getBlockLight(x & 0xf, z & 0xf, y & 0x7f));
    }

    // redstone-related shenanigans
    // currently not implemented

    public boolean isBlockPowered() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBlockIndirectlyPowered() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBlockFacePowered(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getBlockPower(BlockFace face) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getBlockPower() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
