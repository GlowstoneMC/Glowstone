package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

public class GlowGhast extends GlowMonster implements Ghast {

    private int explosionPower;

    public GlowGhast(Location loc) {
        super(loc, EntityType.GHAST, 10);
        setBoundingBox(4, 4);
    }

    public int getExplosionPower() {
        return explosionPower;
    }

    public void setExplosionPower(int explosionPower) {
        this.explosionPower = explosionPower;
    }

    public boolean isAttacking() {
        return metadata.getBoolean(MetadataIndex.GHAST_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        metadata.set(MetadataIndex.GHAST_ATTACKING, attacking);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_GHAST_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_GHAST_HURT;
    }
}
