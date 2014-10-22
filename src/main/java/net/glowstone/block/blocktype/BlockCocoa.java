package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;

import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.MaterialData;

public class BlockCocoa extends BlockAttachable implements IBlockGrowable {

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
