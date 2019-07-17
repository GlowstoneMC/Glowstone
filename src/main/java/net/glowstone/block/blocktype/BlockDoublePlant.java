package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockDoublePlant extends BlockNeedsAttached implements IBlockGrowable {

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        Material type = block.getRelative(BlockFace.DOWN).getType();
        return (type == Material.GRASS_BLOCK || type == Material.DIRT || type == Material.FARMLAND)
                && block.getRelative(BlockFace.UP).getType() == Material.AIR;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        GlowBlockState headBlockState = block.getRelative(BlockFace.UP).getState();
        headBlockState.setType(block.getType());
        Bisected upper = getCastedBlockData(Bisected.class, block.getType().createBlockData());
        upper.setHalf(Half.TOP);
        headBlockState.setBlockData(upper);
        headBlockState.update(true);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        Bisected data = getCastedBlockData(Bisected.class, block.getBlockData());
        if (block.getType() != Material.TALL_GRASS
            && block.getType() != Material.LARGE_FERN
            && data.getHalf() != Half.TOP) {
            return Collections.unmodifiableList(Arrays.asList(new ItemStack(block.getType())));
        }
        return Collections.emptyList();
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        Bisected data = getCastedBlockData(Bisected.class, block.getBlockData());
        if (data.getHalf() == Half.TOP) {
            GlowBlock blockUnder = block.getRelative(BlockFace.DOWN);
            if (!getMaterials().contains(block.getType())) {
                return;
            }
            blockUnder.setType(Material.AIR);
        } else {
            GlowBlock blockTop = block.getRelative(BlockFace.UP);
            if (!getMaterials().contains(block.getType())) {
                return;
            }
            blockTop.setType(Material.AIR);
        }
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        Material species = block.getType();
        return species != Material.TALL_GRASS && species != Material.LARGE_FERN;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        Material species = block.getType();
        switch (species) {
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
                block.getWorld()
                    .dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        BlockType holdingType = ItemTable.instance().getBlock(holding.getType());
        Material species = block.getType();
        if (species == Material.TALL_GRASS
            || species == Material.LARGE_FERN) {
            if (holdingType != null && holdingType.canPlaceAt(null, block, face)) {
                block.getRelative(BlockFace.UP).setType(Material.AIR, (byte) 0, false);
            }
            return true;
        }
        Bisected data = getCastedBlockData(Bisected.class, block.getBlockData());
        if (data.getHalf() == Half.TOP) {
            GlowBlock under = block.getRelative(BlockFace.DOWN);
            if (getMaterials().contains(under.getType())) {
                Material underSpecies = under.getType();
                if (underSpecies == Material.TALL_GRASS
                    || underSpecies == Material.LARGE_FERN) {
                    if (holdingType != null && holdingType.canPlaceAt(null, block, face)) {
                        under.setType(Material.AIR, (byte) 0, false);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
