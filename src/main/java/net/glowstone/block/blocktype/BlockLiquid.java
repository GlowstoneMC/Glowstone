package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class BlockLiquid extends BlockType {

    private final Material bucketType;

    protected BlockLiquid(Material bucketType) {
        this.bucketType = bucketType;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Public accessors

    /**
     * Get the bucket type to replace the empty bucket when the liquid has
     * been collected.
     * @return The associated bucket types material
     */
    public Material getBucketType() {
        return this.bucketType;
    }

    /**
     * Check if the BlockState block is collectible by a bucket.
     * @param block The block state to check
     * @return Boolean representing if its collectible
     */
    public abstract boolean isCollectible(GlowBlockState block);

    ////////////////////////////////////////////////////////////////////////////
    // Overrides

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        state.setType(getMaterial());

        // 8 = Full liquid block
        state.setRawData((byte) 8);
    }

}
