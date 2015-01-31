package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowPig;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

class PigStore extends AgeableStore<GlowPig> {

    public PigStore() {
        super(GlowPig.class, "Pig");
    }

    @Override
    public GlowPig createEntity(Location location, CompoundTag compound) {
        return new GlowPig(location);
    }

    public void load(GlowPig entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setSaddle(compound.getBool("Saddle"));
    }

    public void save(GlowPig entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("Saddle", entity.hasSaddle());
    }
}
