package net.glowstone.entity.physics;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A BoundingBox which changes position over time as an entity moves.
 */
public class EntityBoundingBox extends BoundingBox {

    public EntityBoundingBox(@NotNull Location loc, double horizSize, double vertSize) {
        super(loc.getX() - horizSize / 2, loc.getY() - vertSize / 2, loc.getZ() - horizSize / 2,
                loc.getX() + horizSize / 2, loc.getY() + vertSize / 2, loc.getZ() + horizSize / 2);
    }

    /**
     * Creates an instance with the given size.
     *
     * @param width    the size on the X axis
     * @param vertSize the size on the Y axis
     * @param depth    the size on the Z axis
     */
    public EntityBoundingBox(@NotNull Location loc, double width, double vertSize, double depth) {
        super(loc.getX() - width / 2, loc.getY() - vertSize / 2, loc.getZ() - depth / 2,
                loc.getX() + width / 2, loc.getY() + vertSize / 2, loc.getZ() + depth / 2);
    }

    public Vector getSize() {
        return new Vector(getMaxX() - getMinX(), getMaxY() - getMinX(), getMaxZ() - getMinZ());
    }

    /**
     * Moves this box so that its center is the given point.
     *
     * @param x the center X coordinate
     * @param y the center Y coordinate
     * @param z the center Z coordinate
     */
    public void setCenter(double x, double y, double z) {
        shift(getCenterX() - x, getCenterY() - y, getCenterZ() - z);
    }

}
