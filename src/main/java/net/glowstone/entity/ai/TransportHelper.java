package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TransportHelper {

    public static void moveTowards(GlowLivingEntity entity, Location direction) {
        moveTowards(entity, direction, 0.3);
    }

    /**
     * Starts an entity moving horizontally toward a location (may overshoot).
     *
     * @param entity    the entity to move
     * @param direction the destination to move toward
     * @param speed     the speed to move
     */
    public static void moveTowards(GlowLivingEntity entity, Location direction, double speed) {
        Location location = entity.getLocation();
        double deltaX = (direction.getX() - location.getX());
        double deltaZ = (direction.getZ() - location.getZ());
        entity.setSpeed(speed);
        entity.setMovement(new Vector(deltaX, 0, deltaZ));
    }
}
