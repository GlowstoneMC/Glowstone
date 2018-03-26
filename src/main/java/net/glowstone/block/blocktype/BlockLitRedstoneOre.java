package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;
import org.bukkit.event.block.BlockFadeEvent;

public class BlockLitRedstoneOre extends BlockRedstoneOre {

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        GlowBlockState state = block.getState();
        state.setType(Material.REDSTONE_ORE);
        BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
        EventFactory.getInstance().callEvent(fadeEvent);
        if (!fadeEvent.isCancelled()) {
            state.update(true);
        }
    }
}
