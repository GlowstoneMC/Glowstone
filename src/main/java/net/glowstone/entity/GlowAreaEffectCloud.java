package net.glowstone.entity;

import com.flowpowered.network.Message;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class GlowAreaEffectCloud extends GlowEntity implements AreaEffectCloud {

    private static final int NETWORK_TYPE_ID = 3;
    /**
     * Used to implement the reapplication delay. Note that this isn't serialized -- all
     * reapplication delays will effectively end when the chunk unloads.
     */
    private final Map<LivingEntity, Long> temporaryImmunities = new WeakHashMap<>();

    @Override
    public void pulse() {
        super.pulse();
        radius += radiusPerTick;
        waitTime--;
        duration--;
        if (duration <= 0 || radius <= 0) {
            remove();
        }
        if (waitTime <= 0) {
            long currentTick = world.getFullTime();
            for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
                if (entity instanceof LivingEntity
                        && temporaryImmunities.getOrDefault(entity, Long.MIN_VALUE) <= currentTick
                        && location.distanceSquared(entity.getLocation()) < radius * radius) {
                    ((LivingEntity) entity).addPotionEffects(customEffects.values());
                    temporaryImmunities.put((LivingEntity) entity,
                            currentTick + reapplicationDelay);
                }
            }
        }
    }

    private final Map<PotionEffectType, PotionEffect> customEffects = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private int duration;
    @Getter
    @Setter
    private int waitTime;
    @Getter
    @Setter
    private int reapplicationDelay;
    @Getter
    @Setter
    private int durationOnUse;
    @Getter
    @Setter
    private float radius;
    @Getter
    @Setter
    private float radiusOnUse;
    @Getter
    @Setter
    private float radiusPerTick;
    @Getter
    @Setter
    private Particle particle;
    @Getter
    @Setter
    private PotionData basePotionData;
    @Getter
    @Setter
    private ProjectileSource source;
    @Getter
    @Setter
    private Color color;

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
        MetadataMap metadataMap = new MetadataMap(GlowAreaEffectCloud.class);
        metadataMap.set(MetadataIndex.AREAEFFECTCLOUD_COLOR, color);
        metadataMap.set(MetadataIndex.AREAEFFECTCLOUD_RADIUS, radius);
        metadataMap.set(MetadataIndex.AREAEFFECTCLOUD_PARTICLEID, particle.ordinal());
        return Arrays.asList(
                new SpawnObjectMessage(entityId, getUniqueId(), NETWORK_TYPE_ID, location),
                new EntityMetadataMessage(entityId, metadataMap.getEntryList()));
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
    public void clearCustomEffects0() {
        clearCustomEffects();
    }

    @Override
    public void clearCustomEffects() {
        customEffects.clear();
    }
}
