package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;

import org.bukkit.Material;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.MaterialData;

public class BlockCocoa extends BlockAttachable {

    public BlockCocoa() {
        setDrops(new ItemStack(Material.INK_SACK, 1, (short) 3));
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        final MaterialData data = block.getState().getData();
        if (data instanceof CocoaPlant) {
            final CocoaPlant cocoa = (CocoaPlant) data;
            final CocoaPlantSize size = cocoa.getSize();
            if (size != CocoaPlantSize.LARGE && random.nextInt(5) == 0) {
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
            }
        } else {
            warnMaterialData(CocoaPlant.class, data);
        }
    }
}
