package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;

public class GlowPolarBear extends GlowAnimal implements PolarBear {

    public GlowPolarBear(Location location) {
        super(location, EntityType.POLAR_BEAR, 30);
        setBoundingBox(1.3, 1.4);
    }

    public boolean isStanding() {
        return metadata.getBoolean(MetadataIndex.POLARBEAR_STANDING);
    }

    public void setStanding(boolean standing) {
        metadata.set(MetadataIndex.POLARBEAR_STANDING, standing);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_POLAR_BEAR_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_POLAR_BEAR_AMBIENT;
    }
}
