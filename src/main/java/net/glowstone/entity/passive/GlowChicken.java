package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GlowChicken extends GlowAnimal implements Chicken {

    private boolean chickenJockey;
    private int eggLayTime;

    public GlowChicken(Location location) {
        super(location, EntityType.CHICKEN, 4);
        setSize(0.4F, 0.7F);
        generateEggLayDelay();
    }

    public boolean isChickenJockey() {
        return chickenJockey;
    }

    public void setChickenJockey(boolean chickenJockey) {
        this.chickenJockey = chickenJockey;
    }

    public int getEggLayTime() {
        return eggLayTime;
    }

    public void setEggLayTime(int eggLayTime) {
        this.eggLayTime = eggLayTime;
    }

    private void generateEggLayDelay() {
        Random r = new Random();
        setEggLayTime(r.nextInt(20 * 60 * 5) + 20 * 60 * 5);
    }

    @Override
    public void pulse() {
        super.pulse();
        eggLayTime--;
        if (eggLayTime <= 0) {
            getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.EGG, 1));
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
}
