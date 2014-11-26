package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.glowstone.generator.decorators.BlockDecorator;

public class UnderwaterDecorator extends BlockDecorator {

    private static final int MIN_RADIUS = 2;
    private final Material type;
    private int hRadius;
    private int vRadius;
    private boolean replaceShoreBlocks;

    public UnderwaterDecorator(Material type) {
        this.type = type;
        replaceShoreBlocks = true;
    }

    public final UnderwaterDecorator setRadiuses(int hRadius, int vRadius) {
        this.hRadius = hRadius;
        this.vRadius = vRadius;
        return this;
    }

    public final UnderwaterDecorator setPreservesShoreBlocks() {
        replaceShoreBlocks = false;
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = world.getHighestBlockYAt(sourceX, sourceZ) - 1;
        while (world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.STATIONARY_WATER ||
                world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.WATER && sourceY > 1) {
            sourceY--;
        }
        final Material material = world.getBlockAt(sourceX, sourceY, sourceZ).getType();
        if (material == Material.STATIONARY_WATER || material == Material.WATER) {
            int n = random.nextInt(hRadius - MIN_RADIUS) + MIN_RADIUS;
            for (int x = sourceX - n; x <= sourceX + n; x++) {
                for (int z = sourceZ - n; z <= sourceZ + n; z++) {
                    if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= n * n) {
                        for (int y = sourceY - vRadius; y <= sourceY + vRadius; y++) {
                            final Block block = world.getBlockAt(x, y, z);
                            if (block.getType() == Material.DIRT || (replaceShoreBlocks && block.getType() == Material.GRASS)) {
                                block.setType(type);
                            }
                        }
                    }
                }
            }
        }
    }
}
