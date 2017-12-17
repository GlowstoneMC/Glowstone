package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SplashPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

// TODO: stubs
public class GlowSplashPotion extends GlowProjectile implements SplashPotion {
    public GlowSplashPotion(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {

    }

    @Override
    public void collide(LivingEntity entity) {

    }

    @Override
    protected int getObjectId() {
        return 0;
    }

    @Override
    public Collection<PotionEffect> getEffects() {
        return null;
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void setItem(ItemStack itemStack) {

    }
}
