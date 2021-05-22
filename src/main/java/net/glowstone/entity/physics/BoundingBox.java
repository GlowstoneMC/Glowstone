package net.glowstone.entity.physics;

import org.bukkit.util.Vector;

/**
 * A rectangular bounding box with minimum and maximum corners.
 */
public class BoundingBox implements Cloneable {

    public final Vector minCorner = new Vector();
    public final Vector maxCorner = new Vector();

    /**
     * Tests whether two bounding boxes intersect.
     *
     * @param a a bounding box
     * @param b a bounding box
     * @return true if {@code a} and {@code b} intersect; false otherwise
     */
    public static boolean intersects(BoundingBox a, BoundingBox b) {
        Vector minA = a.minCorner;
        Vector maxA = a.maxCorner;
        Vector minB = b.minCorner;
        Vector maxB = b.maxCorner;
        return maxA.getX() >= minB.getX() && minA.getX() <= maxB.getX()
            && maxA.getY() >= minB.getY() && minA.getY() <= maxB.getY()
            && maxA.getZ() >= minB.getZ() && minA.getZ() <= maxB.getZ();
    }

    /**
     * Converts two Vector instances to a BoundingBox.
     *
     * @param a any corner
     * @param b the corner opposite {@code a}
     * @return a bounding box from {@code a} to {@code b}
     */
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

    /**
     * Creates a bounding box given its minimum corner and its size.
     *
     * @param pos  the minimum corner
     * @param size the displacement of the maximum corner from the minimum corner
     * @return a bounding box from {@code pos} to {@code pos.clone().add(size)}
     */
    public static BoundingBox fromPositionAndSize(Vector pos, Vector size) {
        BoundingBox box = new BoundingBox();
        box.minCorner.copy(pos);
        box.maxCorner.copy(pos.clone().add(size));
        return box;
    }

    /**
     * Returns a deep copy of a BoundingBox.
     *
     * @param original the BoundingBox to copy
     * @return a copy of {@code original}
     */
    public static BoundingBox copyOf(BoundingBox original) {
        BoundingBox box = new BoundingBox();
        box.minCorner.copy(original.minCorner);
        box.maxCorner.copy(original.maxCorner);
        return box;
    }

    /**
     * Tests whether this intersects another bounding box.
     *
     * @param other another bounding box
     * @return true if this bounding box and {@code other} intersect; false otherwise
     */
    public final boolean intersects(BoundingBox other) {
        return intersects(this, other);
    }

    /**
     * Returns the displacement of the maximum corner from the minimum corner.
     *
     * @return the displacement of the maximum corner from the minimum corner
     */
    public Vector getSize() {
        return maxCorner.clone().subtract(minCorner);
    }

}
