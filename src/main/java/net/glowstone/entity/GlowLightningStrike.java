package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.physics.BoundingBox;
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A GlowLightning strike is an entity produced during thunderstorms.
 */
public class GlowLightningStrike extends GlowWeather implements LightningStrike {

    /**
     * Whether the lightning strike is just for effect.
     */
    private boolean effect;

    /**
     * How long this lightning strike has to remain in the world.
     */
    private final int ticksToLive;

    private final Random random;

    public GlowLightningStrike(Location location, boolean effect, Random random) {
        super(location);
        this.effect = effect;
        this.ticksToLive = 30;
        this.random = random;
    }

    @Override
    public EntityType getType() {
        return EntityType.LIGHTNING;
    }

    @Override
    public boolean isEffect() {
        return effect;
    }

    @Override
    public void pulse() {
        super.pulse();
        if (getTicksLived() >= ticksToLive) {
            remove();
        }
        if (getTicksLived() == 1) {
            // Play Sound
            location.getWorld().playSound(location, Sound.AMBIENCE_THUNDER, 10000, 0.8F + random.nextFloat() * 0.2F);
            location.getWorld().playSound(location, Sound.EXPLODE, 2, 0.5F + random.nextFloat() * 0.2F);
            if (!effect) { // Deal damage to nearby entities if it's not just a visual effect
                for (Entity entity : getNearbyEntities(3, 6, 3)) {
                    if (entity instanceof Damageable) {
                        ((Damageable) entity).damage(5, this, EntityDamageEvent.DamageCause.LIGHTNING);
                    }
                    entity.setFireTicks(entity.getMaxFireTicks());
                }
            }
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return Arrays.<Message>asList(new SpawnLightningStrikeMessage(id, x, y, z));
    }

    @Override
    public List<Message> createUpdateMessage() {
        return Arrays.asList();
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        // This behavior is similar to CraftBukkit, where a call with args
        // (0, 0, 0) finds any entities whose bounding boxes intersect that of
        // this entity.

        final BoundingBox searchBox = BoundingBox.fromPositionAndSize(location.toVector(), new Vector(0, 0, 0));
        final Vector vec = new Vector(x, y, z);
        final Vector vec2 = new Vector(0, 0.5 * y, 0);
        searchBox.minCorner.subtract(vec).add(vec2);
        searchBox.maxCorner.add(vec).add(vec2);

        return world.getEntityManager().getEntitiesInside(searchBox, this);
    }
}
