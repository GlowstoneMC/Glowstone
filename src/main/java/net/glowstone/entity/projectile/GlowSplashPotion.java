package net.glowstone.entity.projectile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.util.EntityUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SplashPotion;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class GlowSplashPotion extends GlowProjectile implements SplashPotion {
    private static final double MAX_VERTICAL_DISTANCE = 2.125;
    private static final double MAX_DISTANCE = 4.0;
    private static final double MAX_DISTANCE_SQUARED = MAX_DISTANCE * MAX_DISTANCE;
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
        Map<LivingEntity, Double> affectedIntensities = new HashMap<>();
        world.getNearbyEntities(location, MAX_DISTANCE, MAX_VERTICAL_DISTANCE, MAX_DISTANCE)
            .stream()
            .filter(LivingEntity.class::isInstance)
            .forEach(entity -> {
                Location entityLoc = entity.getLocation();
                double distFractionSquared = entityLoc.distanceSquared(location)
                    / MAX_DISTANCE_SQUARED;
                if (distFractionSquared < 1) {
                    // intensity is 1 - (distance / max distance)
                    affectedIntensities.put((LivingEntity) entity,
                        1 - Math.sqrt(distFractionSquared));
                }
            });
        PotionSplashEvent event = EventFactory.getInstance().callEvent(
            new PotionSplashEvent(this, affectedIntensities));
        if (!event.isCancelled()) {
            for (LivingEntity splashed : event.getAffectedEntities()) {
                for (PotionEffect effect : effects) {
                    double intensity = event.getIntensity(splashed);
                    EntityUtils.applyPotionEffectWithIntensity(
                        effect, splashed, intensity, intensity);
                }
            }
        }
        remove();
    }

    @Override
    protected int getObjectId() {
        return EntityNetworkUtil.getObjectId(EntityType.SPLASH_POTION);
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
