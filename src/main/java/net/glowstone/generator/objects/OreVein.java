package net.glowstone.generator.objects;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class OreVein implements TerrainObject {

    private final Material type;
    private final MaterialData data;
    private final int amount;
    private final Material targetType;

    /**
     * Creates the instance for a given ore type.
     *
     * @param oreType the ore type
     */
    public OreVein(OreType oreType) {
        type = oreType.getType();
        data = oreType.getData();
        amount = oreType.getAmount();
        targetType = oreType.getTargetType();
    }

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        float angle = random.nextFloat() * (float) Math.PI;
        double dx1 = sourceX + Math.sin(angle) * amount / 8.0F;
        double dx2 = sourceX - Math.sin(angle) * amount / 8.0F;
        double dz1 = sourceZ + Math.cos(angle) * amount / 8.0F;
        double dz2 = sourceZ - Math.cos(angle) * amount / 8.0F;
        double dy1 = sourceY + random.nextInt(3) - 2;
        double dy2 = sourceY + random.nextInt(3) - 2;
        boolean succeeded = false;
        for (int i = 0; i < amount; i++) {
            double originX = dx1 + (dx2 - dx1) * i / amount;
            double originY = dy1 + (dy2 - dy1) * i / amount;
            double originZ = dz1 + (dz2 - dz1) * i / amount;
            double q = random.nextDouble() * amount / 16.0D;
            double radiusH = (Math.sin(i * (float) Math.PI / amount) + 1 * q + 1) / 2.0D;
            double radiusV = (Math.sin(i * (float) Math.PI / amount) + 1 * q + 1) / 2.0D;
            for (int x = (int) (originX - radiusH); x <= (int) (originX + radiusH); x++) {

                // scale the center of x to the range [-1, 1] within the circle
                double squaredNormalizedX = normalizedSquaredCoordinate(originX, radiusH, x);
                if (squaredNormalizedX >= 1) {
                    continue;
                }
                for (int y = (int) (originY - radiusV); y <= (int) (originY + radiusV); y++) {
                    double squaredNormalizedY = normalizedSquaredCoordinate(originY, radiusV, y);
                    if (squaredNormalizedX + squaredNormalizedY >= 1) {
                        continue;
                    }
                    for (int z = (int) (originZ - radiusH); z <= (int) (originZ + radiusH);
                         z++) {
                        double squaredNormalizedZ
                                = normalizedSquaredCoordinate(originZ, radiusH, z);
                        Block block = world.getBlockAt(x, y, z);
                        if (squaredNormalizedX + squaredNormalizedY + squaredNormalizedZ < 1
                                && block.getType() == targetType) {
                            BlockState state = block.getState();
                            state.setType(type);
                            state.setData(data);
                            state.update(true, false);
                            succeeded = true;
                        }
                    }
                }
            }
        }
        return succeeded;
    }

    /**
     * The square of the percentage of the radius that is the distance between the given block's
     * center and the center of an orthogonal ellipsoid. A block's center is inside the ellipsoid
     * if and only if its normalizedSquaredCoordinate values add up to less than 1.
     *
     * @param origin the center of the spheroid
     * @param radius the spheroid's radius on this axis
     * @param x the raw coordinate
     * @return the square of the normalized coordinate
     */
    protected static double normalizedSquaredCoordinate(double origin, double radius, int x) {
        double squaredNormalizedX = (x + 0.5D - origin) / radius;
        squaredNormalizedX *= squaredNormalizedX;
        return squaredNormalizedX;
    }
}
