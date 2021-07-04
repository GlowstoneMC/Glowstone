package net.glowstone.generator.objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.Random;

public class IceSpike implements TerrainObject {

    private static final Material[] MATERIALS = {Material.AIR, Material.DIRT, Material.SNOW,
        Material.SNOW_BLOCK, Material.ICE};
    private static final int MAX_STEM_RADIUS = 1;
    private static final int MAX_STEM_HEIGHT = 50;

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        int tipHeight = random.nextInt(4) + 7;
        int tipRadius = tipHeight / 4 + random.nextInt(2);
        int tipOffset = random.nextInt(4);
        if (tipRadius > 1 && random.nextInt(60) == 0) {
            // sometimes generate a giant spike
            tipOffset += random.nextInt(30) + 10;
        }
        boolean succeeded = false;
        int stemRadius = Math.max(0, Math.min(MAX_STEM_RADIUS, tipRadius - 1));
        for (int x = -stemRadius; x <= stemRadius; x++) {
            for (int z = -stemRadius; z <= stemRadius; z++) {
                int stackHeight = MAX_STEM_HEIGHT;
                if (Math.abs(x) == MAX_STEM_RADIUS && Math.abs(z) == MAX_STEM_RADIUS) {
                    stackHeight = random.nextInt(5);
                }
                for (int y = tipOffset - 1; y >= -3; y--) {
                    Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                    if (Arrays.asList(MATERIALS).contains(block.getType())
                        || block.getType() == Material.PACKED_ICE) {
                        block.setType(Material.PACKED_ICE);
                        stackHeight--;
                        if (stackHeight <= 0) {
                            y -= random.nextInt(5);
                            stackHeight = random.nextInt(5);
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        for (int y = 0; y < tipHeight; y++) {
            float f = (1.0F - (float) y / tipHeight) * tipRadius;
            int radius = (int) Math.ceil(f);
            for (int x = -radius; x <= radius; x++) {
                float fx = -0.25F - x;
                for (int z = -radius; z <= radius; z++) {
                    float fz = -0.25F - z;
                    if ((x != 0 || z != 0) && (fx * fx + fz * fz > f * f || (
                            (x == Math.abs(radius) || z == Math.abs(radius))
                                    && random.nextFloat() > 0.75F))) {
                        continue;
                    }
                    // tip shape in top direction
                    Block block = world
                        .getBlockAt(sourceX + x, sourceY + tipOffset + y, sourceZ + z);
                    if (Arrays.asList(MATERIALS).contains(block.getType())) {
                        block.setType(Material.PACKED_ICE);
                        succeeded = true;
                    }
                    if (radius > 1 && y != 0) { // same shape in bottom direction
                        block = world
                            .getBlockAt(sourceX + x, sourceY + tipOffset - y, sourceZ + z);
                        if (Arrays.asList(MATERIALS).contains(block.getType())) {
                            block.setType(Material.PACKED_ICE);
                            succeeded = true;
                        }
                    }
                }
            }
        }
        return succeeded;
    }
}
