package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowman;

public class GlowSnowman extends GlowMonster implements Snowman {

    public GlowSnowman(Location loc) {
        super(loc, EntityType.SNOWMAN, 4);
        setBoundingBox(0.7, 1.9);
    }

    @Override
    public boolean isDerp() {
        return metadata.getBit(MetadataIndex.SNOWMAN_NOHAT, 0x1);
    }

    @Override
    public void setDerp(boolean derp) {
        metadata.setBit(MetadataIndex.SNOWMAN_NOHAT, 0x1, derp);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SNOW_GOLEM_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SNOW_GOLEM_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SNOW_GOLEM_AMBIENT;
    }

    @Override
    public void rangedAttack(LivingEntity target, float charge) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setChargingAttack(boolean raiseHands) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
