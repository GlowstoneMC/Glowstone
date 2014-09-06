package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import org.bukkit.event.inventory.InventoryType;

public class TEFurnace extends TEContainer {

    public TEFurnace(GlowBlock block) {
        super(block, InventoryType.FURNACE);
        setSaveId("Furnace");
    }

}
