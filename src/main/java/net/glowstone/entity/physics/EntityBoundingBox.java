package net.glowstone.entity.physics;

import org.bukkit.util.Vector;

/**
 * A BoundingBox which changes position over time as an entity moves.
 */
public class EntityBoundingBox extends BoundingBox {

    private final double width, vSize, depth;

    public EntityBoundingBox(double hSize, double vSize) {
        this(hSize, vSize, hSize);
    }

    public EntityBoundingBox(double width, double vSize, double depth) {
        this.width = width;
        this.vSize = vSize;
        this.depth = depth;
    }

    @Override
    public Vector getSize() {
        return new Vector(width, vSize, depth);
    }

    public void setCenter(double x, double y, double z) {
        minCorner.setX(x - width / 2);
        minCorner.setY(y);
        minCorner.setZ(z - depth / 2);
        maxCorner.setX(x + width / 2);
        maxCorner.setY(y + vSize);
        maxCorner.setZ(z + depth / 2);
    }

}
