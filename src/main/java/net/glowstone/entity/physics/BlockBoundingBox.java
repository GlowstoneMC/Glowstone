package net.glowstone.entity.physics;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BlockBoundingBox extends BoundingBox {

    /**
     * Creates a bounding box that is effectively the entire given block.
     *
     * @param block the block
     */
    public BlockBoundingBox(Block block) {
        minCorner.setX(block.getX());
        minCorner.setY(block.getY());
        minCorner.setZ(block.getZ());
        maxCorner.setX(block.getX() + 1);
        maxCorner.setY(block.getY() + 0.95);
        maxCorner.setZ(block.getZ() + 1);
    }

    @Override
    public Vector getSize() {
        return new Vector(1, 1, 1);
    }
}
