package net.glowstone.entity.physics;

import org.bukkit.util.Vector;

/**
 * A BoundingBox which changes position over time as an entity moves.
 */
public final class EntityBoundingBox extends BoundingBox {

    private final double hSize, vSize;

    public EntityBoundingBox(double hSize, double vSize) {
        this.hSize = hSize;
        this.vSize = vSize;
    }

    @Override
    public Vector getSize() {
        return new Vector(hSize, vSize, hSize);
    }

    public void setCenter(double x, double y, double z) {
        minCorner.setX(x - hSize / 2);
        minCorner.setY(y);
        minCorner.setZ(z - hSize / 2);
        maxCorner.setX(x + hSize / 2);
        maxCorner.setY(y + vSize);
        maxCorner.setZ(z + hSize / 2);
    }

}
