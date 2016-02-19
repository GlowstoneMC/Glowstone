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
        return metadata.getBit(MetadataIndex.WOLF_FLAGS, TameableFlags.WOLF_IS_ANGRY);
    }

    @Override
    public void setAngry(boolean angry) {
        metadata.setBit(MetadataIndex.WOLF_FLAGS, TameableFlags.WOLF_IS_ANGRY, angry);
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
        return metadata.getByte(MetadataIndex.WOLF_BEGGING) == 1;
    }

    public void setBegging(boolean begging) {
        metadata.set(MetadataIndex.WOLF_BEGGING, (byte) (begging ? 1 : 0));
    }

    @Override
    public void setTamed(boolean isTamed) {
        metadata.setBit(MetadataIndex.WOLF_FLAGS, TameableFlags.IS_TAME, isTamed);
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
        if (animalTamer != null) {
            metadata.set(MetadataIndex.WOLF_OWNER, animalTamer.getName());
        }
    }

    @Override
    public void setHealth(double health) {
        metadata.set(MetadataIndex.WOLF_HEALTH, (float) health);
        super.setHealth(health);
    }
}
