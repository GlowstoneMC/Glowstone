package net.glowstone.entity.projectile;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TippedArrow;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

// TODO: stubs
public class GlowTippedArrow extends GlowArrow implements TippedArrow {

    public GlowTippedArrow(Location location) {
        super(location);
    }

    @Override
    public void setBasePotionData(PotionData potionData) {

    }

    @Override
    public PotionData getBasePotionData() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public boolean hasCustomEffects() {
        return false;
    }

    @Override
    public List<PotionEffect> getCustomEffects() {
        return null;
    }

    @Override
    public boolean addCustomEffect(PotionEffect potionEffect, boolean b) {
        return false;
    }

    @Override
    public boolean removeCustomEffect(PotionEffectType potionEffectType) {
        return false;
    }

    @Override
    public boolean hasCustomEffect(PotionEffectType potionEffectType) {
        return false;
    }

    @Override
    public void clearCustomEffects() {

    }
}
