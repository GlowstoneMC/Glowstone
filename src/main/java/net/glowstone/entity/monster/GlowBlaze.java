package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;

public class GlowBlaze extends GlowMonster implements Blaze {

    public GlowBlaze(Location loc) {
        super(loc, EntityType.BLAZE, 20);
        setBoundingBox(0.6, 1.8);
    }

    public boolean isOnFire() {
        return metadata.getByte(MetadataIndex.BLAZE_ON_FIRE) == 1;
    }

    public void setOnFire(boolean onFire) {
        metadata.set(MetadataIndex.BLAZE_ON_FIRE, onFire ? (byte) 1 : (byte) 0);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_BLAZE_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_BLAZE_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_BLAZE_AMBIENT;
    }
}
