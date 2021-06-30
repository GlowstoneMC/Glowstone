package net.glowstone.entity.physics;

import org.bukkit.util.Vector;

/**
 * A BoundingBox which changes position over time as an entity moves.
 */
public class EntityBoundingBox extends BoundingBox {

    private final double width;
    private final double vertSize;
    private final double depth;

    public EntityBoundingBox(double horizSize, double vertSize) {
        this(horizSize, vertSize, horizSize);
    }

    /**
     * Creates an instance with the given size.
     *
     * @param width    the size on the X axis
     * @param vertSize the size on the Y axis
     * @param depth    the size on the Z axis
     */
    public EntityBoundingBox(double width, double vertSize, double depth) {
        this.width = width;
        this.vertSize = vertSize;
        this.depth = depth;
    }

    @Override
    public Vector getSize() {
        return new Vector(width, vertSize, depth);
    }

    /**
     * Moves this box so that its center is the given point.
     *
     * @param x the center X coordinate
     * @param y the center Y coordinate
     * @param z the center Z coordinate
     */
    public void setCenter(double x, double y, double z) {
        minCorner.setX(x - width / 2);
        minCorner.setY(y);
        minCorner.setZ(z - depth / 2);
        maxCorner.setX(x + width / 2);
        maxCorner.setY(y + vertSize);
        maxCorner.setZ(z + depth / 2);
    }

}
