package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GlowLingeringPotion extends GlowSplashPotion implements LingeringPotion {
    public GlowLingeringPotion(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {
        super.collide(block);
        createEffectCloud();
    }

    @Override
    public void collide(LivingEntity entity) {
        super.collide(entity);
        createEffectCloud();
    }

    private void createEffectCloud() {
        AreaEffectCloud cloud = (AreaEffectCloud)
                location.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        cloud.setRadius(3);
        cloud.setRadiusPerTick(-.005f);
        ItemMeta meta = getItem().getItemMeta();
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            cloud.setColor(potionMeta.getColor());
            cloud.setBasePotionData(potionMeta.getBasePotionData());
            for (PotionEffect effect : getEffects()) {
                cloud.addCustomEffect(effect, true);
            }
        }
        remove();
    }
}
