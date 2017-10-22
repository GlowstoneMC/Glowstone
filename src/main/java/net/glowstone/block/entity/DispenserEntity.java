package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.state.GlowDispenser;
import net.glowstone.inventory.GlowInventory;
import org.bukkit.event.inventory.InventoryType;

public class DispenserEntity extends ContainerEntity {

    public DispenserEntity(GlowBlock block) {
        super(block, new GlowInventory(new GlowDispenser(block), InventoryType.DISPENSER));
        setOwnSaveId();
    }

    protected void setOwnSaveId() {
        setSaveId("minecraft:dispenser");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowDispenser(block);
    }
}
