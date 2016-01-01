package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowChicken;
import net.glowstone.util.nbt.CompoundTag;

class ChickenStore extends AgeableStore<GlowChicken> {

    public ChickenStore() {
        super(GlowChicken.class, "Chicken");
    }

    @Override
    public void load(GlowChicken entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isCompound("isChickenJockey")) entity.setChickenJockey(compound.getBool("isChickenJockey"));
        entity.setEggLayTime(compound.getInt("EggLayTime"));
    }

    @Override
    public void save(GlowChicken entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("isChickenJockey", entity.isChickenJockey());
        tag.putInt("EggLayTime", entity.getEggLayTime());
    }

}
