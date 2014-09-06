package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import org.bukkit.event.inventory.InventoryType;

public class TEDropper extends TEDispenser {

    public TEDropper(GlowBlock block) {
        super(block, InventoryType.DROPPER);
        setSaveId("Dropper");
    }
}
