package net.glowstone.io.entity;

import net.glowstone.entity.GlowTNTPrimed;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

public class TNTPrimedStorage extends EntityStore<GlowTNTPrimed> {

    public TNTPrimedStorage() {
        super(GlowTNTPrimed.class, "PrimedTnt");
    }

    @Override
    public GlowTNTPrimed createEntity(Location location, CompoundTag compound) {
        return new GlowTNTPrimed(location, null);
    }

    @Override
    public void load(GlowTNTPrimed entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isByte("Fuse")) {
            entity.setFuseTicks(tag.getByte("Fuse"));
        }
    }

    @Override
    public void save(GlowTNTPrimed entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putByte("Fuse", entity.getFuseTicks());
    }
}
