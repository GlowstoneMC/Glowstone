package net.glowstone.io.entity;

import java.util.UUID;
import java.util.stream.Collectors;
import net.glowstone.entity.GlowAreaEffectCloud;
import net.glowstone.inventory.GlowMetaPotion;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionData;
import org.bukkit.projectiles.ProjectileSource;

class AreaEffectCloudStore extends EntityStore<GlowAreaEffectCloud> {

    public AreaEffectCloudStore() {
        super(GlowAreaEffectCloud.class, EntityType.AREA_EFFECT_CLOUD);
    }

    @Override
    public void load(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.load(entity, tag);
        // TODO: Age, Color, Duration, ReapplicationDelay, WaitTime, OwnerUUIDLeast, OwnerUUIDMost,
        // DurationOnUse, Radius, RadiusOnUse, RadiusPerTick, Particle, ParticleParam1,
        // ParticleParam2, Potion, Effects
    }

    @Override
    public void save(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Age", entity.getTicksLived());
        Color color = entity.getColor();
        if (color != null) {
            tag.putInt("Color", color.asRGB());
        }
        tag.putInt("Duration", entity.getDuration());
        tag.putInt("ReapplicationDelay", entity.getReapplicationDelay());
        tag.putInt("WaitTime", entity.getWaitTime());
        ProjectileSource source = entity.getSource();
        if (source instanceof Entity) {
            UUID uuid = ((Entity) source).getUniqueId();
            tag.putLong("OwnerUUIDLeast", uuid.getLeastSignificantBits());
            tag.putLong("OwnerUUIDMost", uuid.getMostSignificantBits());
        }
        tag.putFloat("DurationOnUse", entity.getDurationOnUse());
        tag.putFloat("Radius", entity.getRadius());
        tag.putFloat("RadiusOnUse", entity.getRadiusOnUse());
        tag.putFloat("RadiusPerTick", entity.getRadiusPerTick());
        Particle particle = entity.getParticle();
        if (particle != null) {
            tag.putString("Particle", particle.toString());
        }
        PotionData potion = entity.getBasePotionData();
        if (potion != null) {
            tag.putString("Potion", GlowMetaPotion.dataToString(potion));
        }
        tag.putCompoundList("Effects", entity
                .getCustomEffects()
                .stream()
                .map(GlowMetaPotion::toNbt)
                .collect(Collectors.toList()));
        // TODO: Are ParticleParam1 and ParticleParam2 unused?
    }

    @Override
    public GlowAreaEffectCloud createEntity(Location location, CompoundTag compound) {
        GlowAreaEffectCloud cloud = new GlowAreaEffectCloud(location);
        load(cloud, compound);
        return cloud;
    }
}
