package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;

public class GlowGuardian extends GlowMonster implements Guardian {

    public GlowGuardian(Location loc) {
        this(loc, EntityType.GUARDIAN, 30);
        setBoundingBox(0.85, 0.85);
    }

    public GlowGuardian(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }

    @Override
    public boolean isElder() {
        //TODO - 1.11 Field has been removed
        //return metadata.getBit(MetadataIndex.GUARDIAN_FLAGS, 0x04);
        return false;
    }

    @Override
    public void setElder(boolean elder) {
        //TODO - 1.11 Field has been removed
        //metadata.setBit(MetadataIndex.GUARDIAN_FLAGS, 0x04, elder);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_GUARDIAN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_GUARDIAN_DEATH;
    }
}
