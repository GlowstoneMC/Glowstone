package net.glowstone.entity.projectile;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SplashPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class GlowSplashPotion extends GlowProjectile implements SplashPotion {
    private static final double MAX_VERTICAL_DISTANCE = 2.125;
    private static final double MAX_DISTANCE_SQUARED = 16.0;
    @Getter
    @Setter
    private ItemStack item;

    public GlowSplashPotion(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {
        applyEffects();
    }

    @Override
    public void collide(LivingEntity entity) {
        applyEffects();
    }

    private void applyEffects() {
        Collection<PotionEffect> effects = getEffects();
        if (effects.isEmpty()) {
            return;
        }
        double y = location.getY();
        for (LivingEntity entity : world.getLivingEntities()) {
            Location entityLocation = entity.getLocation();
            double verticalOffset = entityLocation.getY() - y;
            if (verticalOffset <= MAX_VERTICAL_DISTANCE
                    && verticalOffset >= -MAX_VERTICAL_DISTANCE
                    && entityLocation.distanceSquared(location) < MAX_DISTANCE_SQUARED) {
                entity.addPotionEffects(effects);
            }
        }
        remove();
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.SPLASH_POTION;
    }

    @Override
    public Collection<PotionEffect> getEffects() {
        if (item == null) {
            return Collections.emptyList();
        }
        ItemMeta meta = item.getItemMeta();
        return meta instanceof PotionMeta ? ((PotionMeta) meta).getCustomEffects()
                : Collections.emptyList();
    }
}
