package net.glowstone.entity.passive;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.inventory.GlowLlamaInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;

/**
 * Represents a llama.
 * The data comes from https://minecraft.gamepedia.com/Llama
 */
public class GlowLlama extends GlowChestedHorse<GlowLlamaInventory> implements Llama {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.HAY_BLOCK);

    private static final Map<Material, Integer> GROWING_FOODS = ImmutableMap
            .<Material, Integer>builder()
            .put(Material.WHEAT, 200)
            .put(Material.HAY_BLOCK, 1800)
            .build();

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
        if (isCarryingChest()) {
            inventory = createNewInventory();
        }
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
    protected GlowLlamaInventory createNewInventory() {
        GlowLlamaInventory oldInventory = inventory;
        GlowLlamaInventory newInventory
                = new GlowLlamaInventory(this, isCarryingChest() ? 3 * getStrength() : 0);
        if (oldInventory != null) {
            newInventory.setSaddle(oldInventory.getSaddle());
            newInventory.setDecor(oldInventory.getDecor());
            moveChestContents(oldInventory, newInventory);
        }
        return newInventory;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }

    @Override
    protected int computeGrowthAmount(Material material) {
        if (!isAdult()) {
            Integer mapResult = GROWING_FOODS.get(material);

            if (mapResult != null) {
                return Math.min(mapResult, Math.abs(getAge()));
            }
        }

        return 0;
    }

}
