package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowChest;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.event.inventory.InventoryType;

/**
 * Tile entity for Chests.
 */
public class TEChest extends TEContainer {

    public TEChest(GlowBlock block) {
        super(block, new GlowInventory(new GlowChest(block), InventoryType.CHEST));
        setSaveId("Chest");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowChest(block);
    }
}
