package net.glowstone.scheduler;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import org.bukkit.Material;

public class PulseTask implements Runnable {
    private final GlowBlock block;
    private final Material originalMaterial;

    public PulseTask(GlowBlock block) {
        this.block = block;
        this.originalMaterial = block.getType();
    }

    @Override
    public void run() {
        if (block.getType() != originalMaterial) {
            return;
        }
        ItemTable table = ItemTable.instance();
        BlockType type = table.getBlock(block.getType());
        if (type != null) {
            type.receivePulse(block);
        }
    }
}
