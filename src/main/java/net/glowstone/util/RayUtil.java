package net.glowstone.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class RayUtil {
    public static Vector getVelocityRay(Vector rayLength) {
        if (rayLength.lengthSquared() == 0) {
            rayLength = new Vector(0, 1, 0);
        } else {
            rayLength.normalize();
        }

        return rayLength;
    }


    public static Vector distanceToHead(Location loc1, Location loc2) {
        return loc1.clone().subtract(loc2).toVector();
    }
}
