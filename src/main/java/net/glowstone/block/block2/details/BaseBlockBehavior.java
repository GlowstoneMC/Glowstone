package net.glowstone.block.block2.details;

import io.netty.util.Signal;
import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.block2.BlockBehavior;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * BlockBehavior for child behaviors to extend.
 */
public class BaseBlockBehavior implements BlockBehavior {

    static final Signal NEXT = Signal.valueOf("BaseBlockBehavior.NEXT");

    protected BaseBlockBehavior() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        throw NEXT;
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        throw NEXT;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        throw NEXT;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        throw NEXT;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        throw NEXT;
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        throw NEXT;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        throw NEXT;
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        throw NEXT;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        throw NEXT;
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        throw NEXT;
    }

    @Override
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType, byte data) {
        throw NEXT;
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        throw NEXT;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods

    /**
     * Display the warning for finding the wrong MaterialData subclass.
     * @param clazz The expected subclass of MaterialData.
     * @param data The actual MaterialData found.
     */
    protected void warnMaterialData(Class<?> clazz, MaterialData data) {
        GlowServer.logger.warning("Wrong MaterialData for " + getClass().getSimpleName() + ": expected " + clazz.getSimpleName() + ", got " + data);
    }

    /**
     * Gets the BlockFace opposite of the direction the location is facing.
     * Usually used to set the way container blocks face when being placed.
     * @param location Location to get opposite of
     * @param inverted If up/down should be used
     * @return Opposite BlockFace or EAST if yaw is invalid
     */
    protected static BlockFace getOppositeBlockFace(Location location, boolean inverted) {
        double rot = location.getYaw() % 360;
        if (inverted) {
            // todo: Check the 67.5 pitch in source. This is based off of WorldEdit's number for this.
            double pitch = location.getPitch();
            if (pitch < -67.5D) {
                return BlockFace.DOWN;
            } else if (pitch > 67.5D) {
                return BlockFace.UP;
            }
        }
        if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 45) {
            return BlockFace.NORTH;
        } else if (45 <= rot && rot < 135) {
            return BlockFace.EAST;
        } else if (135 <= rot && rot < 225) {
            return BlockFace.SOUTH;
        } else if (225 <= rot && rot < 315) {
            return BlockFace.WEST;
        } else if (315 <= rot && rot < 360.0) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }
}
