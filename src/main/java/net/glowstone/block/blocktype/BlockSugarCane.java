package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockSugarCane extends BlockNeedsAttached {

    private static final BlockFace[] DIRECT_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH};

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Block below = block.getRelative(BlockFace.DOWN);
        Material type = below.getType();
        switch (type) {
            case SUGAR_CANE_BLOCK:
                return true;
            case DIRT:
            case GRASS:
            case SAND:
                return isNearWater(below);
        }
        return false;
    }


    private boolean isNearWater(Block block) {
        for (BlockFace face : DIRECT_FACES) {
            switch (block.getRelative(face).getType()) {
                case WATER:
                case STATIONARY_WATER:
                    return true;
            }
        }

        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.SUGAR_CANE)));
    }
}
