package net.glowstone.entity.passive;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

//import net.glowstone.entity.meta.MetadataIndex.TameableFlags;

public class GlowWolf extends GlowTameable implements Wolf {

    @Getter
    private DyeColor collarColor;

    /**
     * Creates a wolf with a random collar color.
     *
     * @param location the location
     */
    public GlowWolf(Location location) {
        super(location, EntityType.WOLF, 8);
        collarColor = DyeColor
            .getByDyeData((byte) ThreadLocalRandom.current().nextInt(DyeColor.values().length));
        setBoundingBox(0.6, 0.85);
    }

    @Override
    public boolean isAngry() {
        return false;
        //metadata.getBit(MetadataIndex.WOLF_FLAGS, TameableFlags.WOLF_IS_ANGRY);
        // TODO 1.9 - Angry seems missing from the metadata according to wiki.vg
    }

    @Override
    public void setAngry(boolean angry) {
        //metadata.setBit(MetadataIndex.WOLF_FLAGS, TameableFlags.WOLF_IS_ANGRY, angry);
        // TODO 1.9 - Angry seems missing from the metadata according to wiki.vg
    }

    @Override
    public void setCollarColor(DyeColor color) {
        metadata.set(MetadataIndex.WOLF_COLOR, color.getDyeData());
        collarColor = color;
    }

    public boolean isBegging() {
        return metadata.getBoolean(MetadataIndex.WOLF_BEGGING);
    }

    public void setBegging(boolean begging) {
        metadata.set(MetadataIndex.WOLF_BEGGING, begging);
    }

    @Override
    public void setTamed(boolean isTamed) {
        if (tamed != isTamed) {
            // Change max health of wolf when he's got tamed. See MinecraftWiki for more information
            if (isTamed && getMaxHealth() == 8) {
                setMaxHealth(20);
                setHealth(20);
            }
        }
        super.setTamed(isTamed);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        // TODO
        super.setOwner(animalTamer);
    }

    @Override
    public void setHealth(double health) {
        metadata.set(MetadataIndex.WOLF_HEALTH, (float) health);
        super.setHealth(health);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_WOLF_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_WOLF_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_WOLF_AMBIENT;
    }
}
