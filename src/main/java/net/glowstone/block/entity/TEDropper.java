package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowDropper;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.event.inventory.InventoryType;

public class TEDropper extends TEContainer {

    public TEDropper(GlowBlock block) {
        super(block, new GlowInventory(new GlowDropper(block), InventoryType.DROPPER));
        setSaveId("Dropper");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowDropper(block);
    }
}
