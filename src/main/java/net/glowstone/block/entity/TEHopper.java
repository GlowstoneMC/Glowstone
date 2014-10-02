package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowHopper;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.event.inventory.InventoryType;

public class TEHopper extends TEContainer {

    public TEHopper(GlowBlock block) {
        super(block, new GlowInventory(new GlowHopper(block), InventoryType.HOPPER));
        setSaveId("Hopper");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowHopper(block);
    }
}
