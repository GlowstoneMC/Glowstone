package net.glowstone.generator.objects;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
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
            for (int x = (int) (originX - radiusH); x <= (int) (originX - radiusH); x++) {
                double px = (x + 0.5D - originX) / radiusH;
                px *= px;
                if (px >= 1) {
                    continue;
                }
                for (int y = (int) (originY - radiusV); y <= (int) (originY + radiusV); y++) {
                    double py = (y + 0.5D - originY) / radiusV;
                    py *= py;
                    if (px + py >= 1) {
                        continue;
                    }
                    for (int z = (int) (originZ - radiusH); z <= (int) (originZ + radiusH);
                         z++) {
                        double pz = (z + 0.5D - originZ) / radiusH;
                        pz *= pz;
                        if (px + py + pz < 1
                                && world.getBlockAt(x, y, z).getType() == targetType) {
                            BlockState state = world.getBlockAt(x, y, z).getState();
                            state.setType(type);
                            state.setData(data);
                            state.update(true);
                            succeeded = true;
                        }
                    }
                }
            }
        }
        return succeeded;
    }
}
