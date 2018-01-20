package net.glowstone.block.entity;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.util.nbt.CompoundTag;

public class BeaconEntity extends BlockEntity {

    private String lock = null; // todo: support item locks
    @Getter
    @Setter
    private int levels = 0;
    @Getter
    @Setter
    private int primaryId = 0;
    @Getter
    @Setter
    private int secondaryId = 0;

    public BeaconEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:beacon");
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
}
