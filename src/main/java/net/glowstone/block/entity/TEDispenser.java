package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import org.bukkit.event.inventory.InventoryType;

public class TEDispenser extends TEContainer {

    public TEDispenser(GlowBlock block) {
        super(block, InventoryType.DISPENSER);
        setSaveId("Trap");
    }

    protected TEDispenser(GlowBlock block, InventoryType subtype) {
        super(block, subtype);
    }

}
