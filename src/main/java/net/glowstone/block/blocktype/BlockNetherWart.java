package net.glowstone.block.blocktype;

import org.bukkit.NetherWartsState;
import org.bukkit.event.block.BlockGrowEvent;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;

public class BlockNetherWart extends BlockType {

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        int cropState = block.getData();
        if (cropState < NetherWartsState.RIPE.ordinal()) {
            cropState++;
            final GlowBlockState state = block.getState();
            state.setRawData((byte) cropState);
            BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
            EventFactory.callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                state.update(true);
            }
        }
    }
}
