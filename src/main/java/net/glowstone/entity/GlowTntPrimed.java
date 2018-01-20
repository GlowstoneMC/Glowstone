package net.glowstone.entity;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.Explosion;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

public class GlowTntPrimed extends GlowExplosive implements TNTPrimed {

    private int fuseTicks;
    private Entity source;

    /**
     * Creates a primed TNT block.
     *
     * @param location the location
     * @param source the entity that ignited this
     */
    public GlowTntPrimed(Location location, Entity source) {
        super(location, Explosion.POWER_TNT);
        setSize(0.98f, 0.98f);

        fuseTicks = 0;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int multiplier = rand.nextBoolean() ? 1 : -1;
        double x = 0;
        double z = 0;
        double mag = rand.nextDouble(0, 0.02);
        if (rand.nextBoolean()) {
            x = multiplier * mag;
        } else {
            z = multiplier * mag;
        }
        setVelocity(new Vector(x, 0.2, z));
        this.source = source;
    }

    /**
     * Sets whether this TNT was ignited by an explosion or not. This is not tracked, but affects
     * the fuse length.
     *
     * @param ignitedByExplosion whether this TNT was ignited by an explosion
     */
    public void setIgnitedByExplosion(boolean ignitedByExplosion) {
        if (ignitedByExplosion) {
            // if ignited by an explosion, the fuseTicks should be a random number between 10 and 30
            // ticks
            fuseTicks = ThreadLocalRandom.current().nextInt(10, 31);
        } else {
            fuseTicks = 80;
        }
    }

    @Override
    public void pulse() {
        super.pulse();

        fuseTicks--;
        if (fuseTicks <= 0) {
            explode();
        } else {
            world.playEffect(location.clone().add(0, 0.5, 0), Effect.SMOKE, 0);
        }
    }

    private void explode() {
        ExplosionPrimeEvent event = EventFactory.callEvent(new ExplosionPrimeEvent(this));

        if (!event.isCancelled()) {
            Location location = getLocation();
            world.createExplosion(this,
                    location.getX(), location.getY() + 0.06125, location.getZ(),
                    event.getRadius(), event.getFire(), true);
        }

        remove();
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Collections.singletonList(new SpawnObjectMessage(
                entityId, getUniqueId(), 50, location));
    }

    @Override
    public final int getFuseTicks() {
        return fuseTicks;
    }

    @Override
    public final void setFuseTicks(int i) {
        fuseTicks = i;
    }

    @Override
    public final Entity getSource() {
        return source.isValid() ? source : null;
    }

    @Override
    public final EntityType getType() {
        return EntityType.PRIMED_TNT;
    }
}
