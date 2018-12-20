package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;

public class BlockCrops extends BlockNeedsAttached implements IBlockGrowable {

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return block.getRelative(BlockFace.DOWN).getType() == Material.SOIL;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getData() >= CropState.RIPE.ordinal()) {
            return Collections.unmodifiableList(
                Arrays.asList(new ItemStack(Material.SEEDS, ThreadLocalRandom.current().nextInt(4)),
                    new ItemStack(Material.WHEAT, 1)));
        } else {
            return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.SEEDS, 1)));
        }
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        return block.getData() != CropState.RIPE.ordinal();
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        GlowBlockState state = block.getState();
        int cropState = block.getData()
            + ThreadLocalRandom.current().nextInt(CropState.MEDIUM.ordinal())
            + CropState.VERY_SMALL.ordinal();
        if (cropState > CropState.RIPE.ordinal()) {
            cropState = CropState.RIPE.ordinal();
        }
        state.setRawData((byte) cropState);
        BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
        EventFactory.getInstance().callEvent(growEvent);
        if (!growEvent.isCancelled()) {
            state.update(true);
        }
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        GlowBlockState state = block.getState();
        int cropState = block.getData();
        // we check light level on the above block, meaning the crops need at least one free block
        // above them in order to grow naturally (vanilla behavior)
        if (cropState < CropState.RIPE.ordinal()
                && block.getRelative(BlockFace.UP).getLightLevel() >= 9
                && ThreadLocalRandom.current()
                        .nextInt((int) (25.0F / getGrowthRateModifier(block)) + 1) == 0) {
            cropState++;
            if (cropState > CropState.RIPE.ordinal()) {
                cropState = CropState.RIPE.ordinal();
            }
            state.setRawData((byte) cropState);
            BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
            EventFactory.getInstance().callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                state.update(true);
            }
        }
        // we check for insufficient light on the block itself, then drop
        if (block.getLightLevel() < 8) {
            block.breakNaturally();
        }
    }

    protected float getGrowthRateModifier(GlowBlock block) {
        float modifier = 1;

        // check for soil around (increase the chance modifier to 10 in the best conditions)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                GlowBlock b = block.getWorld()
                    .getBlockAt(block.getX() + x, block.getY() - 1, block.getZ() + z);
                float soilBonus = 0;
                if (b.getType() == Material.SOIL) {
                    soilBonus = 1;
                    // check if soil is wet for more bonus
                    if (b.getData() > 0) {
                        soilBonus = 3;
                    }
                    // more chances if the soil the crop is planted on is wet
                    if (x != 0 || z != 0) {
                        soilBonus /= 4.0F;
                    }
                }
                // this will add 0.25 points for dry soil around, 0.75 points for wet soil around
                // and 1 point for dry soil under the stem, 3 points for wet soil under stem
                modifier += soilBonus;
            }
        }

        // check for crops around, decrease chances by 50% if a crop of the same type is found on
        // both NS and EW axis, or a crop is found on a diagonal block
        boolean cropOnDiagonalBlock = false;
        boolean cropOnNorthOrSouth = false;
        boolean cropOnEastOrWest = false;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    if (block.getWorld()
                        .getBlockAt(block.getX() + x, block.getY(), block.getZ() + z).getType()
                        == getMaterial()) {
                        if (x != 0 && z != 0) {
                            cropOnDiagonalBlock = true;
                        } else if (x == 0) {
                            cropOnNorthOrSouth = true;
                        } else {
                            cropOnEastOrWest = true;
                        }
                    }
                }
            }
        }

        if (cropOnNorthOrSouth && cropOnEastOrWest || cropOnDiagonalBlock) {
            return modifier / 2.0F;
        }

        return modifier;
    }
}
