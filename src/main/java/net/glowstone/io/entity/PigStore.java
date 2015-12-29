package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowPig;
import net.glowstone.util.nbt.CompoundTag;

class PigStore extends AgeableStore<GlowPig> {

    public PigStore() {
        super(GlowPig.class, "Pig");
    }

    @Override
    public void load(GlowPig entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setSaddle(compound.getBool("Saddle"));
    }

    @Override
    public void save(GlowPig entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("Saddle", entity.hasSaddle());
    }
}
