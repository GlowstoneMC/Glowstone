package net.glowstone.entity;

import com.flowpowered.network.Message;
import net.glowstone.EventFactory;
import net.glowstone.Explosion;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GlowTNTPrimed extends GlowExplosive implements TNTPrimed {

    private int fuseTicks;
    private Entity source;

    public GlowTNTPrimed(Location location, Entity source) {
        super(location, Explosion.POWER_TNT);
        fuseTicks = 0;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int multiplier = rand.nextBoolean() ? 1 : -1;
        double x = 0, z = 0;
        double mag = rand.nextDouble(0, 0.02);
        if (rand.nextBoolean()) {
            x = multiplier * mag;
        } else {
            z = multiplier * mag;
        }
        setVelocity(new Vector(x, 0.2, z));
        this.source = source;
    }

    public void setIgnitedByExplosion(boolean ignitedByExplosion) {
        if (ignitedByExplosion) {
            // if ignited by an explosion, the fuseTicks should be a random number between 10 and 30 ticks
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
            double x = location.getX() + 0.5, y = location.getY() + 0.5, z = location.getZ() + 0.5;
            world.createExplosion(this, x, y, z, event.getRadius(), event.getFire(), true);
        }

        remove();
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int pitch = Position.getIntPitch(location);
        int yaw = Position.getIntYaw(location);

        LinkedList<Message> result = new LinkedList<>();
        result.add(new SpawnObjectMessage(id, getUniqueId(), 50, x, y, z, pitch, yaw));
        return result;
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
    public Location getSourceLoc() {
        return null;
    }

    @Override
    public final EntityType getType() {
        return EntityType.PRIMED_TNT;
    }

    @Override
    public Location getOrigin() {
        return null;
    }
}
