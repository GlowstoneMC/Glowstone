package net.glowstone.entity.passive;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.inventory.GlowLlamaInventory;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;

public class GlowLlama extends GlowChestedHorse<GlowLlamaInventory> implements Llama {

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

    @Override
    protected void createNewInventory() {
        GlowLlamaInventory oldInventory = inventory;
        inventory = new GlowLlamaInventory();
        if (oldInventory != null) {
            inventory.setSaddle(oldInventory.getSaddle());
            inventory.setDecor(oldInventory.getDecor());
        }
    }
}
