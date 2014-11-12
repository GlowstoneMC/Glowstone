package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

public class BlockCocoa extends BlockAttachable implements IBlockGrowable {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        final MaterialData data = state.getData();
        if (data instanceof CocoaPlant) {
            setAttachedFace(state, face.getOppositeFace());
            final CocoaPlant cocoa = (CocoaPlant) data;
            cocoa.setFacingDirection(face);
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        if (block.getRelative(against.getOppositeFace()).getType() == Material.LOG) {
            final MaterialData data = block.getState().getData();
            if (data instanceof Tree) {
                if (((Tree) data).getSpecies() == TreeSpecies.JUNGLE) {
                    return true;
                }
            } else {
                warnMaterialData(Tree.class, data);
            }
        }
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        final MaterialData data = block.getState().getData();
        if (data instanceof CocoaPlant) {
            int amount = 1;
            if (((CocoaPlant) data).getSize() == CocoaPlantSize.LARGE) {
                amount = 3;
            }
            return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.INK_SACK, amount, (short) 3)));
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
        return Collections.unmodifiableList(Arrays.asList(new ItemStack[0]));
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        final MaterialData data = block.getState().getData();
        if (data instanceof CocoaPlant) {
            if (((CocoaPlant) data).getSize() != CocoaPlantSize.LARGE) {
                return true;
            }
        } else {
            warnMaterialData(CocoaPlant.class, data);
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
        if (data instanceof CocoaPlant) {
            final CocoaPlant cocoa = (CocoaPlant) data;
            CocoaPlantSize size = cocoa.getSize();
            if (size == CocoaPlantSize.SMALL) {
                cocoa.setSize(CocoaPlantSize.MEDIUM);
            } else if (size == CocoaPlantSize.MEDIUM) {
                cocoa.setSize(CocoaPlantSize.LARGE);
            } else {
                return;
            }
            final GlowBlockState state = block.getState();
            state.setData(cocoa);
            BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
            EventFactory.callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                state.update(true);
            }
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
    }
}
