package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowBat;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class BatStore extends LivingEntityStore<GlowBat> {

    public BatStore() {
        super(GlowBat.class, EntityType.BAT);
    }

    @Override
    public GlowBat createEntity(Location location, CompoundTag compound) {
        return new GlowBat(location);
    }

    @Override
    public void load(GlowBat entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isByte("BatFlags")) {
            entity.setAwake(compound.getBool("BatFlags"));
        } else {
            entity.setAwake(true);
        }

    }

    @Override
    public void save(GlowBat entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("BatFlags", entity.isAwake());
    }
}
