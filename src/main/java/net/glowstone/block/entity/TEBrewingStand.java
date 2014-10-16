package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowBrewingStand;
import net.glowstone.inventory.GlowBrewerInventory;
import net.glowstone.util.nbt.CompoundTag;

public class TEBrewingStand extends TEContainer {

    private int brewTime = 0;

    public TEBrewingStand(GlowBlock block) {
        super(block, new GlowBrewerInventory(new GlowBrewingStand(block, 0)));
        setSaveId("Cauldron");
    }

    public int getBrewTime() {
        return brewTime;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.isInt("BrewTime")) {
            brewTime = tag.getInt("BrewTime");
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putInt("BrewTime", brewTime);
    }

    @Override
    public GlowBlockState getState() {
        return new GlowBrewingStand(block);
    }
}
