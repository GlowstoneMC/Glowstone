package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Comparator;
import org.bukkit.util.Vector;

public class BlockRedstoneComparator extends BlockNeedsAttached {

    public BlockRedstoneComparator() {
        setDrops(new ItemStack(Material.REDSTONE_COMPARATOR));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        Comparator comparator = (Comparator) block.getState().getData();
        comparator.setSubtractionMode(!comparator.isSubtractionMode());
        block.setData(comparator.getData());
        block.getWorld().requestPulse(block);
        return true;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        Comparator comparator = (Comparator) state.getData();
        comparator.setFacingDirection(player.getCardinalFacing());
        comparator.setSubtractionMode(false);
        state.getBlock().setData(comparator.getData());
        state.getWorld().requestPulse(state.getBlock());
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return true;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return 2;
    }
}
