package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vindicator;

public class GlowVindicator extends GlowIllager implements Vindicator {

    private boolean johnny;

    public GlowVindicator(Location loc) {
        super(loc, EntityType.VINDICATOR, 24);
        setBoundingBox(0.5, 0.8);
    }

    public boolean isJohnny() {
        return johnny;
    }

    public void setJohnny(boolean johnny) {
        this.johnny = johnny;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_VINDICATOR_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_VINDICATOR_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_VINDICATOR_AMBIENT;
    }
}
