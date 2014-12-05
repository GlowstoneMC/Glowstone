package net.glowstone.generator.objects;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockPatch {

    private static final int MIN_RADIUS = 2;
    private final Material type;
    private final int hRadius;
    private final int vRadius;
    private final boolean replaceShoreBlocks;

    public BlockPatch(Material type, int hRadius, int vRadius, boolean replaceShoreBlocks) {
        this.type = type;
        this.hRadius = hRadius;
        this.vRadius = vRadius;
        this.replaceShoreBlocks = replaceShoreBlocks;
    }

    public void generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        int n = random.nextInt(hRadius - MIN_RADIUS) + MIN_RADIUS;
        for (int x = sourceX - n; x <= sourceX + n; x++) {
            for (int z = sourceZ - n; z <= sourceZ + n; z++) {
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= n * n) {
                    for (int y = sourceY - vRadius; y <= sourceY + vRadius; y++) {
                        final Block block = world.getBlockAt(x, y, z);
                        if (block.getType() == Material.DIRT || (replaceShoreBlocks && block.getType() == Material.GRASS)) {
                            block.setType(type);
                            block.setData((byte) 0);
                        }
                    }
                }
            }
        }
    }
}
