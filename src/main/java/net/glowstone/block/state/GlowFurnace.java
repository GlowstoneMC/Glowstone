package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEFurnace;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;

public class GlowFurnace extends GlowBlockState implements Furnace {

    private short burnTime = 0;
    private short cookTime = 0;

    public GlowFurnace(GlowBlock block) {
        super(block);
        burnTime = getTileEntity().getBurnTime();
        cookTime = getTileEntity().getCookTime();
    }

    public GlowFurnace(GlowBlock block, short burnTime, short cookTime) {
        super(block);
        this.burnTime = burnTime;
        this.cookTime = cookTime;
    }

    private TEFurnace getTileEntity() {
        return (TEFurnace) getBlock().getTileEntity();
    }

    @Override
    public short getBurnTime() {
        return burnTime;
    }

    @Override
    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    @Override
    public short getCookTime() {
        return cookTime;
    }

    @Override
    public void setCookTime(short cookTime) {
        this.cookTime = cookTime;
    }

    @Override
    public FurnaceInventory getInventory() {
        return (FurnaceInventory) getTileEntity().getInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            TEFurnace furnace = getTileEntity();
            furnace.setBurnTime(burnTime);
            furnace.setCookTime(cookTime);
        }
        return result;
    }
}
