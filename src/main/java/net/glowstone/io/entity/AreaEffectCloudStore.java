package net.glowstone.io.entity;

import java.util.UUID;
import java.util.stream.Collectors;
import net.glowstone.GlowServer;
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

    private static final String DURATION = "Duration";
    private static final String COLOR = "Color";
    private static final String REAPPLICATION_DELAY = "ReapplicationDelay";
    private static final String WAIT_TIME = "WaitTime";
    private static final String OWNER_UUID_LEAST = "OwnerUUIDLeast";
    private static final String OWNER_UUID_MOST = "OwnerUUIDMost";
    private static final String DURATION_ON_USE = "DurationOnUse";
    private static final String RADIUS = "Radius";
    private static final String RADIUS_ON_USE = "RadiusOnUse";
    private static final String RADIUS_PER_TICK = "RadiusPerTick";
    private static final String PARTICLE = "Particle";
    private static final String POTION = "Potion";
    private static final String EFFECTS = "Effects";

    public AreaEffectCloudStore() {
        super(GlowAreaEffectCloud.class, EntityType.AREA_EFFECT_CLOUD);
    }

    @Override
    public void load(GlowAreaEffectCloud entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readInt(COLOR, rgb -> entity.setColor(Color.fromRGB(rgb)));
        tag.readInt(DURATION, entity::setDuration);
        tag.readInt(REAPPLICATION_DELAY, entity::setReapplicationDelay);
        tag.readInt(WAIT_TIME, entity::setWaitTime);
        // TODO: OWNER_UUID_LEAST, OWNER_UUID_MOST
        tag.readInt(DURATION_ON_USE, entity::setDurationOnUse);
        tag.readFloat(RADIUS, entity::setRadius);
        tag.readFloat(RADIUS_ON_USE, entity::setRadiusOnUse);
        tag.readFloat(RADIUS_PER_TICK, entity::setRadiusPerTick);
        tag.readString(PARTICLE, particle -> {
            try {
                entity.setParticle(Particle.valueOf(particle));
            } catch (IllegalArgumentException e) {
                GlowServer.logger.warning(() -> String.format(
                        "Ignoring invalid particle type %s in tag %s", particle, tag));
            }
        });
        // TODO: Potion
        tag.readCompoundList(EFFECTS, effects -> effects
            .stream()
            .map(GlowMetaPotion::fromNbt)
            .forEach(effect -> entity.addCustomEffect(effect, false)));
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
