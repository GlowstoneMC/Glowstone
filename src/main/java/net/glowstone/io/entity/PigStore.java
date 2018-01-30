package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowPig;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class PigStore extends AgeableStore<GlowPig> {

    public PigStore() {
        super(GlowPig.class, EntityType.PIG, GlowPig::new);
    }

    @Override
    public void load(GlowPig entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isByte("Saddle")) {
            entity.setSaddle(compound.getBool("Saddle"));
        } else {
            entity.setSaddle(false);
        }
    }

    @Override
    public void save(GlowPig entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("Saddle", entity.hasSaddle());
    }
}
