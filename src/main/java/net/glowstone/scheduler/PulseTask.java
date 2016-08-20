package net.glowstone.scheduler;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;

public class PulseTask extends GlowTask {
    public PulseTask(GlowBlock block) {
        super(null, () -> {
            ItemTable table = ItemTable.instance();
            BlockType type = table.getBlock(block.getType());
            type.receivePulse(block);
        }, true, 1, 1);
    }
}
