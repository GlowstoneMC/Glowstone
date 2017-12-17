package net.glowstone.entity.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TippedArrow;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// TODO: stubs
public class GlowTippedArrow extends GlowArrow implements TippedArrow {

    private Color color;
    private PotionData basePotionData;
    private final Map<PotionEffectType, PotionEffect> customEffects = new ConcurrentHashMap<>();

    public GlowTippedArrow(Location location) {
        super(location);
    }

    @Override
    public void collide(LivingEntity entity) {
        super.collide(entity);
        entity.addPotionEffects(customEffects.values());
    }

    @Override
    public void setBasePotionData(PotionData potionData) {
        basePotionData = potionData;
    }

    @Override
    public PotionData getBasePotionData() {
        return basePotionData;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean hasCustomEffects() {
        return !customEffects.isEmpty();
    }

    @Override
    public List<PotionEffect> getCustomEffects() {
        return new ArrayList<>(customEffects.values());
    }

    @Override
    public boolean addCustomEffect(PotionEffect potionEffect, boolean overwrite) {
        PotionEffectType type = potionEffect.getType();
        if (overwrite) {
            customEffects.put(type, potionEffect);
            return true;
        } else {
            return customEffects.putIfAbsent(type, potionEffect) == null;
        }
    }

    @Override
    public boolean removeCustomEffect(PotionEffectType potionEffectType) {
        return customEffects.remove(potionEffectType) != null;
    }

    @Override
    public boolean hasCustomEffect(PotionEffectType potionEffectType) {
        return customEffects.containsKey(potionEffectType);
    }

    @Override
    public void clearCustomEffects() {
        customEffects.clear();
    }
}
