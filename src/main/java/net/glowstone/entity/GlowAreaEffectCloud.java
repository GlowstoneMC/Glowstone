package net.glowstone.entity;

import com.flowpowered.network.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

// TODO: stubs
public class GlowAreaEffectCloud extends GlowEntity implements AreaEffectCloud {
    private final Map<PotionEffectType, PotionEffect> customEffects = new ConcurrentHashMap<>();

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param location The location of the entity.
     */
    public GlowAreaEffectCloud(Location location) {
        super(location);
    }

    @Override
    public List<Message> createSpawnMessage() {
        return null;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void setDuration(int i) {

    }

    @Override
    public int getWaitTime() {
        return 0;
    }

    @Override
    public void setWaitTime(int i) {

    }

    @Override
    public int getReapplicationDelay() {
        return 0;
    }

    @Override
    public void setReapplicationDelay(int i) {

    }

    @Override
    public int getDurationOnUse() {
        return 0;
    }

    @Override
    public void setDurationOnUse(int i) {

    }

    @Override
    public float getRadius() {
        return 0;
    }

    @Override
    public void setRadius(float v) {

    }

    @Override
    public float getRadiusOnUse() {
        return 0;
    }

    @Override
    public void setRadiusOnUse(float v) {

    }

    @Override
    public float getRadiusPerTick() {
        return 0;
    }

    @Override
    public void setRadiusPerTick(float v) {

    }

    @Override
    public Particle getParticle() {
        return null;
    }

    @Override
    public void setParticle(Particle particle) {

    }

    @Override
    public void setBasePotionData(PotionData potionData) {

    }

    @Override
    public PotionData getBasePotionData() {
        return null;
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

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public ProjectileSource getSource() {
        return null;
    }

    @Override
    public void setSource(ProjectileSource projectileSource) {

    }
}
