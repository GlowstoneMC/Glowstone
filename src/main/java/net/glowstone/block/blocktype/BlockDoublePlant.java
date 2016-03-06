package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockDoublePlant extends BlockNeedsAttached implements IBlockGrowable {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Material type = block.getRelative(BlockFace.DOWN).getType();
        return (type == Material.GRASS || type == Material.DIRT || type == Material.SOIL) &&
                block.getRelative(BlockFace.UP).getType() == Material.AIR;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        GlowBlockState headBlockState = block.getRelative(BlockFace.UP).getState();
        headBlockState.setType(Material.DOUBLE_PLANT);
        headBlockState.setData(new DoublePlant(DoublePlantSpecies.PLANT_APEX));
        headBlockState.update(true);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        MaterialData data = block.getState().getData();
        if (data instanceof DoublePlant) {
            DoublePlantSpecies species = ((DoublePlant) data).getSpecies();
            if (species != DoublePlantSpecies.DOUBLE_TALLGRASS && species != DoublePlantSpecies.LARGE_FERN && species != DoublePlantSpecies.PLANT_APEX) {
                return Collections.unmodifiableList(Arrays.asList(data.toItemStack(1)));
            }
        } else {
            warnMaterialData(DoublePlant.class, data);
        }
        return BlockDropless.EMPTY_STACK;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        MaterialData data = block.getState().getData();
        if (data instanceof DoublePlant) {
            DoublePlantSpecies species = ((DoublePlant) data).getSpecies();
            block.setType(Material.AIR);
            if (species == DoublePlantSpecies.PLANT_APEX) {
                block = block.getRelative(BlockFace.DOWN);
                if (!(block.getState().getData() instanceof DoublePlant)) {
                    return;
                }
            } else {
                block = block.getRelative(BlockFace.UP);
                if (!(block.getState().getData() instanceof DoublePlant)) {
                    return;
                }
            }
            block.setType(Material.AIR);
        } else {
            warnMaterialData(DoublePlant.class, data);
        }
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        MaterialData data = block.getState().getData();
        if (data instanceof DoublePlant) {
            if (((DoublePlant) data).getSpecies() == DoublePlantSpecies.PLANT_APEX) {
                data = block.getRelative(BlockFace.DOWN).getState().getData();
                if (!(data instanceof DoublePlant)) {
                    return false;
                }
            }
            DoublePlantSpecies species = ((DoublePlant) data).getSpecies();
            if (species != DoublePlantSpecies.DOUBLE_TALLGRASS && species != DoublePlantSpecies.LARGE_FERN) {
                return true;
            }
        } else {
            warnMaterialData(DoublePlant.class, data);
        }
        return false;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        MaterialData data = block.getState().getData();
        if (data instanceof DoublePlant) {
            if (((DoublePlant) data).getSpecies() == DoublePlantSpecies.PLANT_APEX) {
                data = block.getRelative(BlockFace.DOWN).getState().getData();
                if (!(data instanceof DoublePlant)) {
                    return;
                }
            }
            DoublePlantSpecies species = ((DoublePlant) data).getSpecies();
            switch (species) {
                case SUNFLOWER:
                case LILAC:
                case ROSE_BUSH:
                case PEONY:
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(data.toItemStack(1)));
                    break;
                default:
                    break;
            }
        } else {
            warnMaterialData(DoublePlant.class, data);
        }
    }
}
