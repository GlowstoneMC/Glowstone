package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.util.nbt.CompoundTag;

public class TEBeacon extends TileEntity {

    private String lock = null; // todo: support item locks
    private int levels = 0, primaryId = 0, secondaryId = 0;

    public TEBeacon(GlowBlock block) {
        super(block);
        setSaveId("beacon");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.isString("Lock")) {
            this.lock = tag.getString("Lock");
        }
        this.levels = tag.getInt("Levels");
        this.primaryId = tag.getInt("Primary");
        this.secondaryId = tag.getInt("Secondary");
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        if (lock != null) {
            tag.putString("Lock", lock);
        }
        tag.putInt("Levels", levels);
        tag.putInt("Primary", primaryId);
        tag.putInt("Secondary", secondaryId);
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public int getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(int primaryId) {
        this.primaryId = primaryId;
    }

    public int getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(int secondaryId) {
        this.secondaryId = secondaryId;
    }
}
