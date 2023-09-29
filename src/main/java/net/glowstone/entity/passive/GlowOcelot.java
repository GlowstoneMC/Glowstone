package net.glowstone.entity.passive;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

import java.util.Set;

public class GlowOcelot extends GlowTameable implements Ocelot {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.COD,
        Material.SALMON,
        Material.PUFFERFISH,
        Material.TROPICAL_FISH);

    @Getter
    private Type catType;

    /**
     * Creates a wild ocelot.
     *
     * @param location the location
     */
    public GlowOcelot(Location location) {
        super(location, EntityType.OCELOT, 10);
        setCatType(Type.WILD_OCELOT);
        setBoundingBox(0.6, 0.8);
    }

    @Override
    public boolean isTrusting() {
        return false;
    }

    @Override
    public void setTrusting(boolean trust) {

    }

    @Override
    public void setCatType(Type type) {
        catType = type;
        metadata.set(MetadataIndex.OCELOT_TYPE, type.getId());
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        // TODO
        super.setOwner(animalTamer);
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_CAT_HURT;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_CAT_DEATH;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_CAT_AMBIENT;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
