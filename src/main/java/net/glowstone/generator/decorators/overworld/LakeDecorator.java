package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.glowstone.generator.decorators.BlockDecorator;

public class LakeDecorator extends BlockDecorator {

    private static final double MAX_DIAMETER = 16.0D;
    private static final double MAX_HEIGHT = 8.0D;
    private static final int MAX_BLOCKS = (int) (MAX_DIAMETER * MAX_DIAMETER * MAX_HEIGHT);
    private final Material type;

    public LakeDecorator(Material type) {
        if (type != Material.STATIONARY_WATER && type != Material.STATIONARY_LAVA) {
            throw new IllegalArgumentException("Lake material must be STATIONARY_WATER or STATIONARY_LAVA");
        }
        this.type = type;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        if (random.nextInt(type == Material.STATIONARY_WATER ? 4 : 8) > 0) {
            return;
        }
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(type == Material.STATIONARY_WATER ? 256 : random.nextInt(248) + 8);
        while (world.getBlockAt(sourceX, sourceY, sourceZ).getType() == Material.AIR
                && sourceY > 5) {
            sourceY--;
        }
        if (sourceY < 5) {
            return;
        }
        sourceY -= MAX_HEIGHT / 2;

        final byte[] lakeMap = new byte[MAX_BLOCKS];
        for (int n = 0; n < random.nextInt(4) + 4; n++) {
            double sizeX = random.nextDouble() * 6.0D + 3;
            double sizeY = random.nextDouble() * 4.0D + 2;
            double sizeZ = random.nextDouble() * 6.0D + 3;
            double dX = random.nextDouble() * (MAX_DIAMETER - sizeX - 2) + 1 + sizeX / 2.0D;
            double dY = random.nextDouble() * (MAX_HEIGHT - sizeY - 4) + 2 + sizeY / 2.0D;
            double dZ = random.nextDouble() * (MAX_DIAMETER - sizeZ - 2) + 1 + sizeZ / 2.0D;
            for (int x = 1; x < MAX_DIAMETER - 1; x++) {
                for (int z = 1; z < MAX_DIAMETER - 1; z++) {
                    for (int y = 1; y < MAX_HEIGHT - 1; y++) {
                        double nX = (x - dX) / (sizeX / 2.0D);
                        nX *= nX;
                        double nY = (y - dY) / (sizeY / 2.0D);
                        nY *= nY;
                        double nZ = (z - dZ) / (sizeZ / 2.0D);
                        nZ *= nZ;
                        if (nX + nY + nZ < 1.0D) {
                            setLakeBlock(lakeMap, x, y, z);
                        }
                    }
                }
            }
        }

        if (!canPlaceLake(lakeMap, world, sourceX, sourceY, sourceZ)) {
            return;
        }

        for (int x = 0; x < MAX_DIAMETER; x++) {
            for (int z = 0; z < MAX_DIAMETER; z++) {
                for (int y = 0; y < MAX_HEIGHT; y++) {
                    if (isLakeBlock(lakeMap, x, y, z)) {
                        Material type = this.type;
                        if (y >= MAX_HEIGHT / 2) {
                            type = Material.AIR;
                        }
                        world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z).setType(type);
                    }
                }
            }
        }
    }

    private boolean canPlaceLake(byte[] lakeMap, World world, int sourceX, int sourceY, int sourceZ) {
        for (int x = 0; x < MAX_DIAMETER; x++) {
            for (int z = 0; z < MAX_DIAMETER; z++) {
                for (int y = 0; y < MAX_HEIGHT; y++) {
                    if (!isLakeBlock(lakeMap, x, y, z) &&
                            ((x < MAX_DIAMETER - 1 && isLakeBlock(lakeMap, x + 1, y, z)) ||
                            (x > 0 && isLakeBlock(lakeMap, x - 1, y, z)) ||
                            (z < MAX_DIAMETER - 1 && isLakeBlock(lakeMap, x, y, z + 1)) ||
                            (z > 0 && isLakeBlock(lakeMap, x, y, z - 1)) ||
                            (z < MAX_HEIGHT - 1 && isLakeBlock(lakeMap, x, y + 1, z)) ||
                            (z > 0 && isLakeBlock(lakeMap, x, y - 1, z)))) {
                        final Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        if (y >= MAX_HEIGHT / 2 && block.isLiquid()) {
                            return false; // there's already some liquids above
                        } else if (y < MAX_HEIGHT / 2 && !block.getType().isSolid() && block.getType() != type) {
                            return false; // bottom must be solid and do not overlap with another liquid type
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isLakeBlock(byte[] lakeMap, int x, int y, int z) {
        return lakeMap[(x * (int) MAX_DIAMETER + z) * (int) MAX_HEIGHT + y] != 0;
    }

    private void setLakeBlock(byte[] lakeMap, int x, int y, int z) {
        lakeMap[(int) ((x * (int) MAX_DIAMETER + z) * (int) MAX_HEIGHT + y)] = 1;
    }
}
