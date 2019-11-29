package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BlockCocoa extends BlockNeedsAttached implements IBlockGrowable {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        state.setType(getMaterial());
        MaterialData data = state.getData();
        if (data instanceof CocoaPlant) {
            CocoaPlant cocoa = (CocoaPlant) data;
            cocoa.setFacingDirection(face.getOppositeFace());
            cocoa.setSize(CocoaPlantSize.SMALL);
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
    }

    @Override
    protected BlockFace getAttachedFace(GlowBlock me) {
        return ((CocoaPlant) me.getState().getData()).getFacing();
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        BlockFace face = against.getOppositeFace();
        return Arrays.asList(SIDES).contains(face)
                && block.getRelative(face).getType() == Material.JUNGLE_LOG;
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        MaterialData data = block.getState().getData();
        if (data instanceof CocoaPlant) {
            int amount = 1;
            if (((CocoaPlant) data).getSize() == CocoaPlantSize.LARGE) {
                amount = 3;
            }
            return Collections.unmodifiableList(
                Arrays.asList(new ItemStack(Material.INK_SAC, amount, (short) 3)));
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        MaterialData data = block.getState().getData();
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
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        MaterialData data = block.getState().getData();
        if (data instanceof CocoaPlant) {
            CocoaPlant cocoa = (CocoaPlant) data;
            CocoaPlantSize size = cocoa.getSize();
            if (size == CocoaPlantSize.SMALL) {
                cocoa.setSize(CocoaPlantSize.MEDIUM);
            } else if (size == CocoaPlantSize.MEDIUM) {
                cocoa.setSize(CocoaPlantSize.LARGE);
            } else {
                return;
            }
            GlowBlockState state = block.getState();
            state.setData(cocoa);
            BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
            EventFactory.getInstance().callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                state.update(true);
            }
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
    }

    @Override
    public void updateBlock(GlowBlock block) {
        MaterialData data = block.getState().getData();
        if (data instanceof CocoaPlant) {
            CocoaPlant cocoa = (CocoaPlant) data;
            CocoaPlantSize size = cocoa.getSize();
            if (size != CocoaPlantSize.LARGE && ThreadLocalRandom.current().nextInt(5) == 0) {
                if (size == CocoaPlantSize.SMALL) {
                    cocoa.setSize(CocoaPlantSize.MEDIUM);
                } else if (size == CocoaPlantSize.MEDIUM) {
                    cocoa.setSize(CocoaPlantSize.LARGE);
                } else {
                    return;
                }
                GlowBlockState state = block.getState();
                state.setData(cocoa);
                BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
                EventFactory.getInstance().callEvent(growEvent);
                if (!growEvent.isCancelled()) {
                    state.update(true);
                }
            }
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
    }
}
