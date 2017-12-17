package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowExperienceOrb;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class ExperienceOrbStore extends EntityStore<GlowExperienceOrb> {

    public ExperienceOrbStore() {
        super(GlowExperienceOrb.class, EntityType.EXPERIENCE_ORB);
    }

    @Override
    public GlowExperienceOrb createEntity(Location location, CompoundTag compound) {
        return new GlowExperienceOrb(location);
    }

    @Override
    public void load(GlowExperienceOrb entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isShort("Age")) {
            entity.setTicksLived(tag.getShort("Age"));
        }
        if (tag.isShort("Value")) {
            entity.setExperience(tag.getShort("Value"));
        }
    }

    @Override
    public void save(GlowExperienceOrb entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putShort("Age", entity.getTicksLived());
        tag.putShort("Value", entity.getExperience());
    }
}
