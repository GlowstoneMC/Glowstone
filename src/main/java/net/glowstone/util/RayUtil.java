package net.glowstone.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class RayUtil {

    public static Vector getVelocityRay(Vector ray) {
        Vector velocityRay = ray.clone();
        if (velocityRay.lengthSquared() == 0) {
            velocityRay.setX(0);
            velocityRay.setY(1);
            velocityRay.setZ(0);
        } else {
            velocityRay.normalize();
        }

        return velocityRay;
    }

    public static float getExposure(Location target, Location... sources) {
        return 1;
    }

    public static Vector getRayBetween(Location target, Location source) {
        return target.clone().subtract(source).toVector();
    }
}
