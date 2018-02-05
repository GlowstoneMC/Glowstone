package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowChicken;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class ChickenStore extends AgeableStore<GlowChicken> {

    public ChickenStore() {
        super(GlowChicken.class, EntityType.CHICKEN, GlowChicken::new);
    }

    @Override
    public void load(GlowChicken entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isByte("isChickenJockey")) {
            entity.setChickenJockey(compound.getBool("isChickenJockey"));
        } else {
            entity.setChickenJockey(false);
        }

        if (compound.isInt("EggLayTime")) {
            entity.setEggLayTime(compound.getInt("EggLayTime"));
        } else {
            entity.setEggLayTime(6000);
        }

    }

    @Override
    public void save(GlowChicken entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("isChickenJockey", entity.isChickenJockey());
        tag.putInt("EggLayTime", entity.getEggLayTime());
    }

}
