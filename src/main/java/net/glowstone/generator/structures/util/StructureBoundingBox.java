package net.glowstone.generator.structures.util;

import lombok.Getter;
import org.bukkit.util.Vector;

public class StructureBoundingBox {

    @Getter
    private Vector min;
    @Getter
    private Vector max;

    public StructureBoundingBox(Vector min, Vector max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Checks whether the given point is inside a block that intersects this box.
     *
     * @param vec the point to check
     * @return true if this box intersects the block containing {@code vec}
     */
    public boolean isVectorInside(Vector vec) {
        return vec.getBlockX() >= min.getBlockX() && vec.getBlockX() <= max.getBlockX()
                && vec.getBlockY() >= min.getBlockY() && vec.getBlockY() <= max.getBlockY()
                && vec.getBlockZ() >= min.getBlockZ() && vec.getBlockZ() <= max.getBlockZ();
    }

    /**
     * Whether this box intersects the given box.
     *
     * @param boundingBox the box to check intersection with
     * @return true if the given box intersects this box; false otherwise
     */
    public boolean intersectsWith(StructureBoundingBox boundingBox) {
        return boundingBox.getMin().getBlockX() <= max.getBlockX()
                && boundingBox.getMax().getBlockX() >= min.getBlockX()
                && boundingBox.getMin().getBlockY() <= max.getBlockY()
                && boundingBox.getMax().getBlockY() >= min.getBlockY()
                && boundingBox.getMin().getBlockZ() <= max.getBlockZ()
                && boundingBox.getMax().getBlockZ() >= min.getBlockZ();
    }

    /**
     * Whether this box intersects the given vertically-infinite box.
     *
     * @param minX the minimum X coordinate
     * @param minZ the minimum Z coordinate
     * @param maxX the maximum X coordinate
     * @param maxZ the maximum Z coordinate
     * @return true if the given box intersects this box; false otherwise
     */
    public boolean intersectsWith(int minX, int minZ, int maxX, int maxZ) {
        return minX <= max.getBlockX() && maxX >= min.getBlockX()
                && minZ <= max.getBlockZ() && maxZ >= min.getBlockZ();
    }

    /**
     * Changes this bounding box to the bounding box of the union of itself and another bounding
     * box.
     *
     * @param boundingBox the other bounding box to contain
     */
    public void expandTo(StructureBoundingBox boundingBox) {
        min = new Vector(Math.min(min.getBlockX(), boundingBox.getMin().getBlockX()),
                Math.min(min.getBlockY(), boundingBox.getMin().getBlockY()),
                Math.min(min.getBlockZ(), boundingBox.getMin().getBlockZ()));
        max = new Vector(Math.max(max.getBlockX(), boundingBox.getMax().getBlockX()),
                Math.max(max.getBlockY(), boundingBox.getMax().getBlockY()),
                Math.max(max.getBlockZ(), boundingBox.getMax().getBlockZ()));
    }

    public void offset(Vector offset) {
        min = min.add(offset);
        max = max.add(offset);
    }
}
