package net.glowstone.io.entity;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import net.glowstone.entity.GlowAreaEffectCloud;
import net.glowstone.inventory.GlowMetaPotion;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionData;
import org.bukkit.projectiles.ProjectileSource;

class AreaEffectCloudStore extends EntityStore<GlowAreaEffectCloud> {

    public static final String DURATION = "Duration";
    public static final String COLOR = "Color";
    public static final String REAPPLICATION_DELAY = "ReapplicationDelay";
    public static final String WAIT_TIME = "WaitTime";
    public static final String OWNER_UUID_LEAST = "OwnerUUIDLeast";
    public static final String OWNER_UUID_MOST = "OwnerUUIDMost";
    public static final String DURATION_ON_USE = "DurationOnUse";
    public static final String RADIUS = "Radius";
    public static final String RADIUS_ON_USE = "RadiusOnUse";
    public static final String RADIUS_PER_TICK = "RadiusPerTick";
    public static final String PARTICLE = "Particle";
    public static final String POTION = "Potion";
    public static final String EFFECTS = "Effects";

    public AreaEffectCloudStore() {
        super(GlowAreaEffectCloud.class, EntityType.AREA_EFFECT_CLOUD);
    }

    private static void readIntIfPresent(CompoundTag tag, String key, IntConsumer consumer) {
        if (tag.isInt(key)) {
            consumer.accept(tag.getInt(key));
        }
    }

    @Override
    public void load(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.load(entity, tag);
        readIntIfPresent(tag, COLOR, rgb -> entity.setColor(Color.fromRGB(rgb)));
        readIntIfPresent(tag, DURATION, entity::setDuration);
        readIntIfPresent(tag, REAPPLICATION_DELAY, entity::setReapplicationDelay);
        readIntIfPresent(tag, WAIT_TIME, entity::setWaitTime);
        // TODO: OWNER_UUID_LEAST, OWNER_UUID_MOST
        readIntIfPresent(tag, DURATION_ON_USE, entity::setDurationOnUse);
        readFloatIfPresent(tag, RADIUS, entity::setRadius);
        readFloatIfPresent(tag, RADIUS_ON_USE, entity::setRadiusOnUse);
        readFloatIfPresent(tag, RADIUS_PER_TICK, entity::setRadiusPerTick);
        if (tag.isString(PARTICLE)) {
            final String particle = tag.getString(PARTICLE);

            try {
                entity.setParticle(Particle.valueOf(particle));
            } catch (IllegalArgumentException e) {
                Bukkit.getServer().getLogger().warning("Ignoring invalid particle type "
                        + particle);
            }
        }
        // TODO: Potion
        if (tag.isCompoundList(EFFECTS)) {
            tag.getCompoundList(EFFECTS)
                    .stream()
                    .map(GlowMetaPotion::fromNbt)
                    .forEach(effect -> entity.addCustomEffect(effect, false));
        }
    }

    private static void readFloatIfPresent(CompoundTag tag, String key, Consumer<Float> consumer) {
        if (tag.isFloat(key)) {
            consumer.accept(tag.getFloat(key));
        }
    }

    @Override
    public void save(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Age", entity.getTicksLived());
        Color color = entity.getColor();
        if (color != null) {
            tag.putInt(COLOR, color.asRGB());
        }
        tag.putInt(DURATION, entity.getDuration());
        tag.putInt(REAPPLICATION_DELAY, entity.getReapplicationDelay());
        tag.putInt(WAIT_TIME, entity.getWaitTime());
        ProjectileSource source = entity.getSource();
        if (source instanceof Entity) {
            UUID uuid = ((Entity) source).getUniqueId();
            tag.putLong(OWNER_UUID_LEAST, uuid.getLeastSignificantBits());
            tag.putLong(OWNER_UUID_MOST, uuid.getMostSignificantBits());
        }
        tag.putInt(DURATION_ON_USE, entity.getDurationOnUse());
        tag.putFloat(RADIUS, entity.getRadius());
        tag.putFloat(RADIUS_ON_USE, entity.getRadiusOnUse());
        tag.putFloat(RADIUS_PER_TICK, entity.getRadiusPerTick());
        Particle particle = entity.getParticle();
        if (particle != null) {
            tag.putString(PARTICLE, particle.toString());
        }
        PotionData potion = entity.getBasePotionData();
        if (potion != null) {
            tag.putString(POTION, GlowMetaPotion.dataToString(potion));
        }
        tag.putCompoundList(EFFECTS, entity
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
