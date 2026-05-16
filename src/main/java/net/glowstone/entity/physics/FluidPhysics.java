package net.glowstone.entity.physics;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.util.Vector;

/**
 * Utility class for calculating fluid physics including water flow direction
 * and fluid drag effects on entities.
 */
public final class FluidPhysics {

    private static final BlockFace[] HORIZONTAL_FACES = {
            BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    /**
     * Flow velocity multiplier for water pushing entities.
     * Vanilla uses approximately 0.014 per flow unit.
     */
    private static final double FLOW_VELOCITY_MULTIPLIER = 0.014;

    /**
     * Maximum flow level for water (0 = source, 7 = minimum flow).
     */
    private static final int MAX_FLOW_LEVEL = 7;

    private FluidPhysics() {
    }

    /**
     * Calculate the flow direction vector for water at a given block location.
     * Water flows from higher levels to lower levels, and downward when possible.
     *
     * @param block the block to calculate flow direction for
     * @return a normalized flow direction vector, or zero vector if no flow
     */
    public static Vector getFlowDirection(Block block) {
        if (!isWater(block.getType())) {
            return new Vector(0, 0, 0);
        }

        Vector flowDirection = new Vector(0, 0, 0);
        int centerLevel = getFluidLevel(block);

        // Check horizontal neighbors for flow direction
        for (BlockFace face : HORIZONTAL_FACES) {
            Block neighbor = block.getRelative(face);
            int neighborLevel = getEffectiveFluidLevel(neighbor, block);

            if (neighborLevel >= 0) {
                int levelDiff = neighborLevel - centerLevel;
                // Flow from higher to lower (remember: 0 = full, 7 = least)
                // So if neighbor has lower number, we flow toward it
                if (levelDiff < 0) {
                    Vector direction = faceToVector(face);
                    direction.multiply(-levelDiff);
                    flowDirection.add(direction);
                }
            }
        }

        // Check if water can flow down
        Block below = block.getRelative(BlockFace.DOWN);
        if (isWater(below.getType()) || below.getType() == Material.AIR
                || !below.getType().isSolid()) {
            // Water is falling, add downward flow component
            if (flowDirection.lengthSquared() > 0) {
                flowDirection.normalize();
            }
            flowDirection.add(new Vector(0, -6.0, 0));
        }

        // Normalize and scale
        if (flowDirection.lengthSquared() > 0) {
            flowDirection.normalize();
            flowDirection.multiply(FLOW_VELOCITY_MULTIPLIER);
        }

        return flowDirection;
    }

    /**
     * Get the fluid level at a block. Returns 0 for source blocks, 1-7 for flowing.
     *
     * @param block the block to check
     * @return fluid level (0-7) or -1 if not a fluid
     */
    public static int getFluidLevel(Block block) {
        if (!isWater(block.getType()) && !isLava(block.getType())) {
            return -1;
        }

        BlockData data = block.getBlockData();
        if (data instanceof Levelled) {
            return ((Levelled) data).getLevel();
        }

        // Fallback for older data format
        return 0;
    }

    /**
     * Get the effective fluid level considering the neighbor block.
     * Used for flow calculations.
     *
     * @param neighbor the neighbor block
     * @param center   the center block
     * @return effective fluid level or -1 if not a compatible fluid
     */
    private static int getEffectiveFluidLevel(Block neighbor, Block center) {
        Material neighborType = neighbor.getType();
        Material centerType = center.getType();

        // Check if same fluid type
        if ((isWater(neighborType) && isWater(centerType))
                || (isLava(neighborType) && isLava(centerType))) {
            return getFluidLevel(neighbor);
        }

        // Air or non-solid blocks can be flowed into
        if (neighborType == Material.AIR || !neighborType.isSolid()) {
            return MAX_FLOW_LEVEL + 1;
        }

        return -1;
    }

    /**
     * Check if a material is water.
     *
     * @param material the material to check
     * @return true if water
     */
    public static boolean isWater(Material material) {
        return material == Material.WATER;
    }

    /**
     * Check if a material is lava.
     *
     * @param material the material to check
     * @return true if lava
     */
    public static boolean isLava(Material material) {
        return material == Material.LAVA;
    }

    /**
     * Check if a material is any fluid.
     *
     * @param material the material to check
     * @return true if fluid
     */
    public static boolean isFluid(Material material) {
        return isWater(material) || isLava(material);
    }

    /**
     * Convert a BlockFace to a direction vector.
     *
     * @param face the block face
     * @return direction vector
     */
    private static Vector faceToVector(BlockFace face) {
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    /**
     * Apply fluid drag to a velocity vector based on the fluid type.
     *
     * @param velocity the velocity to modify
     * @param physics  the entity physics configuration
     * @param material the fluid material the entity is in
     */
    public static void applyFluidDrag(Vector velocity, EntityPhysics physics, Material material) {
        double drag;
        if (isWater(material)) {
            drag = physics.getWaterDrag();
        } else if (isLava(material)) {
            drag = physics.getLavaDrag();
        } else {
            return;
        }

        velocity.multiply(drag);
    }

    /**
     * Apply water flow push to an entity's velocity if applicable.
     *
     * @param velocity the velocity to modify
     * @param physics  the entity physics configuration
     * @param block    the block the entity is in
     */
    public static void applyWaterFlowPush(Vector velocity, EntityPhysics physics, Block block) {
        if (!physics.isPushedByWaterFlow()) {
            return;
        }

        if (!isWater(block.getType())) {
            return;
        }

        Vector flowDirection = getFlowDirection(block);
        velocity.add(flowDirection);
    }
}
