package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowBat;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

class BatStore extends LivingEntityStore<GlowBat> {

    public BatStore() {
        super(GlowBat.class, "Bat");
    }

    @Override
    public GlowBat createEntity(Location location, CompoundTag compound) {
        return new GlowBat(location);
    }

    public void load(GlowBat entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setAwake(compound.getByte("BatFlags") == 1);
    }

    public void save(GlowBat entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("BatFlags", entity.isAwake() ? 1 : 0);
    }
}
