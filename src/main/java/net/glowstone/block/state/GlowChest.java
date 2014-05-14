package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEChest;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class GlowChest extends GlowBlockState implements Chest {

    public GlowChest(GlowBlock block) {
        super(block);
    }

    private TEChest getTileEntity() {
        return (TEChest) getBlock().getTileEntity();
    }

    @Override
    public Inventory getBlockInventory() {
        return getTileEntity().getInventory();
    }

    @Override
    public Inventory getInventory() {
        // todo: handle double chests
        return getBlockInventory();
    }
}
