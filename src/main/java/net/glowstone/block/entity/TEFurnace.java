package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowFurnace;
import net.glowstone.inventory.GlowFurnaceInventory;
import net.glowstone.util.nbt.CompoundTag;

public class TEFurnace extends TEContainer {

    private short burnTime = 0;
    private short cookTime = 0;

    public TEFurnace(GlowBlock block) {
        super(block, new GlowFurnaceInventory(new GlowFurnace(block, (short) 0, (short) 0)));
        setSaveId("Furnace");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowFurnace(block);
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putShort("BurnTime", burnTime);
        tag.putShort("CookTime", cookTime);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.isShort("BurnTime")) {
            burnTime = tag.getShort("BurnTime");
        }
        if (tag.isShort("CookTime")) {
            cookTime = tag.getShort("CookTime");
        }
    }

    public short getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    public short getCookTime() {
        return cookTime;
    }

    public void setCookTime(short cookTime) {
        this.cookTime = cookTime;
    }
}
