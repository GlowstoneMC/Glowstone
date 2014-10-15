package net.glowstone.block.blocktype;

import org.bukkit.CropState;
import org.bukkit.event.block.BlockGrowEvent;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;

public class BlockCrops extends BlockType {

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        final GlowBlockState state = block.getState();
        int cropState = block.getData();
        if (cropState < CropState.RIPE.ordinal()) {
            if (random.nextInt(3) == 0) {
               cropState++;
               if (cropState > CropState.RIPE.ordinal()) {
                   cropState = CropState.RIPE.ordinal();
               }
               state.setRawData((byte) cropState);
               BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
               EventFactory.callEvent(growEvent);
               if (!growEvent.isCancelled()) {
                   state.update(true);
               }
            }
        }
    }
}
