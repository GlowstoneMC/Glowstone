package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEHopper;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;

public class GlowHopper extends GlowBlockState implements Hopper {

    public GlowHopper(GlowBlock block) {
        super(block);
    }

    private TEHopper getTileEntity() {
        return (TEHopper) getBlock().getTileEntity();
    }

    @Override
    public Inventory getInventory() {
        return getTileEntity().getInventory();
    }
}
