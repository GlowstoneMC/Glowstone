package net.glowstone.entity.passive;

import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataIndex.TameableFlags;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

import java.util.Random;

public class GlowWolf extends GlowTameable implements Wolf {

    private DyeColor collarColor;

    public GlowWolf(Location location) {
        super(location, EntityType.WOLF, 8);
        Random r = new Random();
        collarColor = DyeColor.getByData((byte) r.nextInt(DyeColor.values().length));
    }

    @Override
    public boolean isAngry() {
        return false;
        //metadata.getBit(MetadataIndex.WOLF_FLAGS, TameableFlags.WOLF_IS_ANGRY); //TODO 1.9 - Angry seems missing from the metadata according to wiki.vg
    }

    @Override
    public void setAngry(boolean angry) {
        //metadata.setBit(MetadataIndex.WOLF_FLAGS, TameableFlags.WOLF_IS_ANGRY, angry); //TODO 1.9 - Angry seems missing from the metadata according to wiki.vg
    }

    @Override
    public DyeColor getCollarColor() {
        return collarColor;
    }

    @Override
    public void setCollarColor(DyeColor color) {
        metadata.set(MetadataIndex.WOLF_COLOR, color.getDyeData());
        this.collarColor = color;
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
            // Change max health of wolf when he's got tamed. See MinecraftWiki for more information!
            if (isTamed && getMaxHealth() == 8) {
                setMaxHealth(20);
                setHealth(20);
            }
        }
        super.setTamed(isTamed);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        super.setOwner(animalTamer);
    }

    @Override
    public void setHealth(double health) {
        metadata.set(MetadataIndex.WOLF_HEALTH, (float) health);
        super.setHealth(health);
    }
}
