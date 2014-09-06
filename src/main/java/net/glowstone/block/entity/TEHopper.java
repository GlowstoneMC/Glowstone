package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import org.bukkit.event.inventory.InventoryType;

public class TEHopper extends TEContainer {

    public TEHopper(GlowBlock block) {
        super(block, InventoryType.HOPPER);
        setSaveId("Hopper");
    }

}
