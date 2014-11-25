package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import net.glowstone.generator.decorators.BlockDecorator;

public class UnderwaterDecorator extends BlockDecorator {

    private static final int MIN_RADIUS = 2;
    private final Material type;
    private final MaterialData data;
    private final int horizontalRadius;
    private final int verticalRadius;
    private final boolean replaceShoreBlocks;

    public UnderwaterDecorator(Material type, int horizontalRadius, int verticalRadius) {
        this(type, horizontalRadius, verticalRadius, true);
    }

    public UnderwaterDecorator(Material type, int horizontalRadius, int verticalRadius, boolean replaceShoreBlocks) {
        this(type, null, horizontalRadius, verticalRadius, replaceShoreBlocks);
    }

    public UnderwaterDecorator(Material type, MaterialData data, int horizontalRadius, int verticalRadius, boolean replaceShoreBlocks) {
        this.type = type;
        this.data = data;
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
        this.replaceShoreBlocks = replaceShoreBlocks;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = world.getHighestBlockYAt(sourceX, sourceZ) - 1;
        final Material material = world.getBlockAt(sourceX, sourceY, sourceZ).getType();
        if (material == Material.WATER || material == Material.STATIONARY_WATER) {
            int n = random.nextInt(horizontalRadius - MIN_RADIUS) + MIN_RADIUS;
            for (int x = sourceX - n; x <= sourceX + n; x++) {
                for (int z = sourceZ - n; z <= sourceZ + n; z++) {
                    if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= n * n) {
                        for (int y = sourceY - verticalRadius; y <= sourceY + verticalRadius; y++) {
                            final BlockState state = world.getBlockAt(x, y, z).getState();
                            if (state.getType() == Material.DIRT || (replaceShoreBlocks && state.getType() == Material.GRASS)) {
                                state.setType(type);
                                if (data != null) {
                                    state.setData(data);
                                }
                                state.update(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
