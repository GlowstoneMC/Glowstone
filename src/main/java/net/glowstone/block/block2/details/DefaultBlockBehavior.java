package net.glowstone.block.block2.details;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.block2.BlockBehavior;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;

/**
 * Default block behavior used when no other behavior is specified.
 */
public final class DefaultBlockBehavior implements BlockBehavior {

    public static final DefaultBlockBehavior instance = new DefaultBlockBehavior();

    private DefaultBlockBehavior() {
    }

    @Override
    public String toString() {
        return "DefaultBlockBehavior";
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(block.getType(), 1, block.getData()));
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return null;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        state.setType(holding.getType());
        state.setRawData((byte) holding.getDurability());
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        return false;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return false;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return block.isLiquid();
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
    }

    @Override
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType, byte data) {
    }

    @Override
    public void updatePhysics(GlowBlock me) {
    }
}
