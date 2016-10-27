package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;

public class GlowGuardian extends GlowMonster implements Guardian {

    public GlowGuardian(Location loc) {
        super(loc, EntityType.GUARDIAN, 30);
        setBoundingBox(0.85, 0.85);
    }

    @Override
    public boolean isElder() {
        return metadata.getBit(MetadataIndex.GUARDIAN_FLAGS, 0x04);
    }

    @Override
    public void setElder(boolean elder) {
        metadata.setBit(MetadataIndex.GUARDIAN_FLAGS, 0x04, elder);
    }

    @Override
    protected Sound getHurtSound() {
        if (isElder()) {
            return Sound.ENTITY_ELDER_GUARDIAN_HURT;
        }
        return Sound.ENTITY_GUARDIAN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        if (isElder()) {
            return Sound.ENTITY_ELDER_GUARDIAN_DEATH;
        }
        return Sound.ENTITY_GUARDIAN_DEATH;
    }
}
