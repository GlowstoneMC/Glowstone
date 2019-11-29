package net.glowstone.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class GlowChicken extends GlowAnimal implements Chicken {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.WHEAT_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.MELON_SEEDS,
            Material.PUMPKIN_SEEDS);

    @Getter
    @Setter
    private boolean chickenJockey;
    @Getter
    @Setter
    private int eggLayTime;

    /**
     * Creates a chicken.
     *
     * @param location the chicken's location
     */
    public GlowChicken(Location location) {
        super(location, EntityType.CHICKEN, 4);
        setSize(0.4F, 0.7F);
        generateEggLayDelay();
    }

    private void generateEggLayDelay() {
        int fiveMinutes = TickUtil.minutesToTicks(5);
        setEggLayTime(ThreadLocalRandom.current().nextInt(fiveMinutes) + fiveMinutes);
    }

    @Override
    public void pulse() {
        super.pulse();
        eggLayTime--;
        if (eggLayTime <= 0) {
            getWorld().dropItem(getLocation(), new ItemStack(Material.EGG, 1));
            getWorld().playSound(getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
            generateEggLayDelay();
        }
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_CHICKEN_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
