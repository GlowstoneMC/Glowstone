package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.DoublePlantSpecies;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockTallGrass extends BlockNeedsAttached implements IBlockGrowable {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (random.nextFloat() < .125) {
            return Collections.unmodifiableList(Collections.singletonList(new ItemStack(Material.SEEDS, 1)));
        }
        return BlockDropless.EMPTY_STACK;
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        final MaterialData data = block.getState().getData();
        if (data instanceof LongGrass) {
            if (((LongGrass) data).getSpecies() != GrassSpecies.DEAD) {
                return true;
            }
        } else {
            warnMaterialData(LongGrass.class, data);
        }
        return false;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        final MaterialData data = block.getState().getData();
        if (data instanceof LongGrass) {
            final GrassSpecies species = ((LongGrass) data).getSpecies();
            if (species == GrassSpecies.NORMAL || species == GrassSpecies.FERN_LIKE) {
                final GlowBlockState headBlockState = block.getRelative(BlockFace.UP).getState();
                if (headBlockState.getType() == Material.AIR) {
                    final DoublePlantSpecies doublePlantSpecies = species == GrassSpecies.FERN_LIKE ?
                            DoublePlantSpecies.LARGE_FERN : DoublePlantSpecies.DOUBLE_TALLGRASS; 
                    final GlowBlockState blockState = block.getState();
                    blockState.setType(Material.DOUBLE_PLANT);
                    blockState.setData(new DoublePlant(doublePlantSpecies));
                    headBlockState.setType(Material.DOUBLE_PLANT);
                    headBlockState.setData(new DoublePlant(DoublePlantSpecies.PLANT_APEX));
                    BlockGrowEvent growEvent = new BlockGrowEvent(block, blockState);
                    EventFactory.callEvent(growEvent);
                    if (!growEvent.isCancelled()) {
                        blockState.update(true);
                        headBlockState.update(true);
                    }
                }
            }
        } else {
            warnMaterialData(LongGrass.class, data);
        }
    }
}
