package net.glowstone.entity.physics;

import org.bukkit.util.Vector;

/**
 * A rectangular bounding box with minimum and maximum corners.
 */
public class BoundingBox implements Cloneable {

    public final Vector minCorner = new Vector();
    public final Vector maxCorner = new Vector();

    public Vector getSize() {
        return maxCorner.clone().subtract(minCorner);
    }

    public final boolean intersects(BoundingBox other) {
        return intersects(this, other);
    }

    public static boolean intersects(BoundingBox a, BoundingBox b) {
        Vector minA = a.minCorner, maxA = a.maxCorner;
        Vector minB = b.minCorner, maxB = b.maxCorner;
        return (maxA.getX() >= minB.getX() && minA.getX() <= maxB.getX() &&
                maxA.getY() >= minB.getY() && minA.getY() <= maxB.getY() &&
                maxA.getZ() >= minB.getZ() && minA.getZ() <= maxB.getZ());
    }

    public static BoundingBox fromCorners(Vector a, Vector b) {
        BoundingBox box = new BoundingBox();
        box.minCorner.setX(Math.min(a.getX(), b.getX()));
        box.minCorner.setY(Math.min(a.getY(), b.getY()));
        box.minCorner.setZ(Math.min(a.getZ(), b.getZ()));
        box.maxCorner.setX(Math.max(a.getX(), b.getX()));
        box.maxCorner.setY(Math.max(a.getY(), b.getY()));
        box.maxCorner.setZ(Math.max(a.getZ(), b.getZ()));
        return box;
    }

    public static BoundingBox fromPositionAndSize(Vector pos, Vector size) {
        BoundingBox box = new BoundingBox();
        box.minCorner.copy(pos);
        box.maxCorner.copy(pos.clone().add(size));
        return box;
    }

    public static BoundingBox copyOf(BoundingBox original) {
        BoundingBox box = new BoundingBox();
        box.minCorner.copy(original.minCorner);
        box.maxCorner.copy(original.maxCorner);
        return box;
    }

}
