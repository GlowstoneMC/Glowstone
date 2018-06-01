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
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DoublePlantSpecies;

public class BlockDoublePlant extends BlockNeedsAttached implements IBlockGrowable {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Material type = block.getRelative(BlockFace.DOWN).getType();
        return (type == Material.GRASS || type == Material.DIRT || type == Material.SOIL)
                && block.getRelative(BlockFace.UP).getType() == Material.AIR;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
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
            if (species != DoublePlantSpecies.DOUBLE_TALLGRASS
                && species != DoublePlantSpecies.LARGE_FERN
                && species != DoublePlantSpecies.PLANT_APEX) {
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
            if (species == DoublePlantSpecies.PLANT_APEX) {
                GlowBlock blockUnder = block.getRelative(BlockFace.DOWN);
                if (!(blockUnder.getState().getData() instanceof DoublePlant)) {
                    return;
                }
                blockUnder.setType(Material.AIR);
            } else {
                GlowBlock blockTop = block.getRelative(BlockFace.UP);
                if (!(blockTop.getState().getData() instanceof DoublePlant)) {
                    return;
                }
                blockTop.setType(Material.AIR);
            }
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
            if (species != DoublePlantSpecies.DOUBLE_TALLGRASS
                && species != DoublePlantSpecies.LARGE_FERN) {
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
                    block.getWorld()
                        .dropItemNaturally(block.getLocation(), new ItemStack(data.toItemStack(1)));
                    break;
                default:
                    break;
            }
        } else {
            warnMaterialData(DoublePlant.class, data);
        }
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        MaterialData data = block.getState().getData();
        BlockType holdingType = ItemTable.instance().getBlock(holding.getType());
        if (data instanceof DoublePlant) {
            DoublePlantSpecies species = ((DoublePlant) data).getSpecies();
            if (species == DoublePlantSpecies.DOUBLE_TALLGRASS
                || species == DoublePlantSpecies.LARGE_FERN) {
                if (holdingType != null && holdingType.canPlaceAt(block, face)) {
                    block.getRelative(BlockFace.UP).setType(Material.AIR, (byte) 0, false);
                }
                return true;
            }
            if (species == DoublePlantSpecies.PLANT_APEX) {
                GlowBlock under = block.getRelative(BlockFace.DOWN);
                MaterialData underData = under.getState().getData();
                if (underData instanceof DoublePlant) {
                    DoublePlantSpecies underSpecies = ((DoublePlant) underData).getSpecies();
                    if (underSpecies == DoublePlantSpecies.DOUBLE_TALLGRASS
                        || underSpecies == DoublePlantSpecies.LARGE_FERN) {
                        if (holdingType != null && holdingType.canPlaceAt(block, face)) {
                            under.setType(Material.AIR, (byte) 0, false);
                        }
                        return true;
                    }
                }
            }
        } else {
            warnMaterialData(DoublePlant.class, data);
        }
        return false;
    }
}
