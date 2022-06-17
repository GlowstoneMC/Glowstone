package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

public class GlowGhast extends GlowMonster implements Ghast {

    @Getter
    @Setter
    private int explosionPower;

    public GlowGhast(Location loc) {
        super(loc, EntityType.GHAST, 10);
        setBoundingBox(4, 4);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_GHAST_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_GHAST_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_GHAST_AMBIENT;
    }

    @Override
    public boolean isCharging() {
        return metadata.getBoolean(MetadataIndex.GHAST_ATTACKING);
    }

    @Override
    public void setCharging(boolean charging) {
        metadata.set(MetadataIndex.GHAST_ATTACKING, charging);
    }
}
