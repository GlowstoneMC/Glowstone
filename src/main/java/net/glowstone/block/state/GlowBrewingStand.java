package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEBrewingStand;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;

public class GlowBrewingStand extends GlowBlockState implements BrewingStand {

    private int brewTime = 0;

    public GlowBrewingStand(GlowBlock block) {
        super(block);
        brewTime = getTileEntity().getBrewTime();
    }

    public GlowBrewingStand(GlowBlock block, int brewTime) {
        super(block);
        this.brewTime = brewTime;
    }

    private TEBrewingStand getTileEntity() {
        return (TEBrewingStand) getBlock().getTileEntity();
    }

    @Override
    public int getBrewingTime() {
        return brewTime;
    }

    @Override
    public void setBrewingTime(int brewTime) {
        this.brewTime = brewTime;
    }

    @Override
    public BrewerInventory getInventory() {
        return (BrewerInventory) getTileEntity().getInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            TEBrewingStand stand = getTileEntity();
            stand.setBrewTime(brewTime);
            stand.updateInRange();
        }
        return result;
    }
}
