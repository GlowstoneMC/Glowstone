package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.state.GlowHopper;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.event.inventory.InventoryType;

public class HopperEntity extends ContainerEntity {

    public HopperEntity(GlowBlock block) {
        super(block, new GlowInventory(new GlowHopper(block), InventoryType.HOPPER));
        setSaveId("minecraft:hopper");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowHopper(block);
    }
}
