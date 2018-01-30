package net.glowstone.entity.passive;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.LlamaInventory;

public class GlowLlama extends GlowChestedHorse implements Llama {

    /**
     * Creates a llama entity.
     *
     * @param location the entity's location
     */
    public GlowLlama(Location location) {
        super(location, EntityType.LLAMA, 22);
        this.setColor(Color.values()[ThreadLocalRandom.current().nextInt(Color.values().length)]);
        setBoundingBox(0.9, 1.87);
    }

    @Override
    public Color getColor() {
        return Color.values()[metadata.getInt(MetadataIndex.LLAMA_VARIANT)];
    }

    @Override
    public void setColor(Color color) {
        metadata.set(MetadataIndex.LLAMA_VARIANT, color.ordinal());
    }

    @Override
    public int getStrength() {
        return metadata.getInt(MetadataIndex.LLAMA_STRENGTH);
    }

    @Override
    public void setStrength(int strength) {
        metadata.set(MetadataIndex.LLAMA_STRENGTH, strength);
    }

    @Override
    public LlamaInventory getInventory() {
        return null; // todo
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_LLAMA_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_LLAMA_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_LLAMA_AMBIENT;
    }
}
