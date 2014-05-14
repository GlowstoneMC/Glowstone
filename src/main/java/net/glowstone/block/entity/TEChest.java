package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowChest;
import org.bukkit.event.inventory.InventoryType;

/**
 * Tile entity for Chests.
 */
public class TEChest extends TEContainer {

    public TEChest(GlowBlock block) {
        super(block, InventoryType.CHEST);
        setSaveId("Chest");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowChest(block);
    }
}
