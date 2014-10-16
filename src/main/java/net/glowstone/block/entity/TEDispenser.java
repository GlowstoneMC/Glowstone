package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowDispenser;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.event.inventory.InventoryType;

public class TEDispenser extends TEContainer {

    public TEDispenser(GlowBlock block) {
        super(block, new GlowInventory(new GlowDispenser(block), InventoryType.DISPENSER));
        setSaveId("Trap");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowDispenser(block);
    }
}
