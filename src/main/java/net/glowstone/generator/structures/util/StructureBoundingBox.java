package net.glowstone.generator.structures.util;

import org.bukkit.util.Vector;

public class StructureBoundingBox {
    private Vector min;
    private Vector max;

    public StructureBoundingBox(Vector min, Vector max) {
        this.min = min;
        this.max = max;
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public boolean isVectorInside(Vector vec) {
        if (vec.getBlockX() >= min.getBlockX() && vec.getBlockX() <= max.getBlockX() &&
                vec.getBlockY() >= min.getBlockY() && vec.getBlockY() <= max.getBlockY() &&
                vec.getBlockZ() >= min.getBlockZ() && vec.getBlockZ() <= max.getBlockZ()) {
            return true;
        }
        return false;
    }

    public boolean intersectsWith(StructureBoundingBox boundingBox) {
        if (boundingBox.min.getBlockX() <= max.getBlockX() && boundingBox.max.getBlockX() >= min.getBlockX() &&
                boundingBox.min.getBlockY() <= max.getBlockY() && boundingBox.max.getBlockY() >= min.getBlockY() &&
                boundingBox.min.getBlockZ() <= max.getBlockZ() && boundingBox.max.getBlockZ() >= min.getBlockZ()) {
            return true;
        }
        return false;
    }

    public boolean intersectsWith(int minX, int minZ, int maxX, int maxZ) {
        if (minX <= max.getBlockX() && maxX >= min.getBlockX() &&
                minZ <= max.getBlockZ() && maxZ >= min.getBlockZ()) {
            return true;
        }
        return false;
    }

    public void expandTo(StructureBoundingBox boundingBox) {
        min = new Vector(Math.min(min.getBlockX(), boundingBox.min.getBlockX()),
                Math.min(min.getBlockY(), boundingBox.min.getBlockY()),
                Math.min(min.getBlockZ(), boundingBox.min.getBlockZ()));
        max = new Vector(Math.max(max.getBlockX(), boundingBox.max.getBlockX()),
                Math.max(max.getBlockY(), boundingBox.max.getBlockY()),
                Math.max(max.getBlockZ(), boundingBox.max.getBlockZ()));
    }

    public void offset(Vector offset) {
        min = min.add(offset);
        max = max.add(offset);
    }
}
