package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TransportHelper {
    public static void moveTowards(GlowLivingEntity entity, Location direction) {
        moveTowards(entity, direction, 0.3);
    }

    public static void moveTowards(GlowLivingEntity entity, Location direction, double speed) {
        Location location = entity.getLocation();
        double deltaX = (direction.getX() - location.getX());
        double deltaZ = (direction.getZ() - location.getZ());
        Vector vector = new Vector(deltaX, 0, deltaZ).normalize().multiply(speed);
        entity.setVelocity(vector);
    }
}
