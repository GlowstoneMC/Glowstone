package net.glowstone.io.entity;

import net.glowstone.entity.GlowTntPrimed;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class TntPrimedStorage extends EntityStore<GlowTntPrimed> {

    public TntPrimedStorage() {
        super(GlowTntPrimed.class, EntityType.PRIMED_TNT);
    }

    @Override
    public GlowTntPrimed createEntity(Location location, CompoundTag compound) {
        return new GlowTntPrimed(location, null);
    }

    @Override
    public void load(GlowTntPrimed entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readByte(entity::setFuseTicks, "Fuse");
    }

    @Override
    public void save(GlowTntPrimed entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Fuse", entity.getFuseTicks());
    }
}
