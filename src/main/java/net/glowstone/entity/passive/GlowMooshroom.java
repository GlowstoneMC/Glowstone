package net.glowstone.entity.passive;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class GlowMooshroom extends GlowAnimal implements MushroomCow {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.WHEAT);
    @Getter
    @Setter
    private MushroomCow.Variant variant;

    public GlowMooshroom(Location location) {
        super(location, EntityType.MUSHROOM_COW, 10);
        setSize(0.9F, 1.3F);
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_COW_HURT;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_COW_DEATH;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_COW_AMBIENT;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }

    @Override
    public int getStewEffectDuration() {
        return 0;
    }

    @Override
    public void setStewEffectDuration(int duration) {

    }

    @Override
    public @Nullable PotionEffectType getStewEffectType() {
        return null;
    }

    @Override
    public void setStewEffect(@Nullable PotionEffectType type) {

    }
}
