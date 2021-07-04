package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

import java.util.Random;

public class AcaciaTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *  @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public AcaciaTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(3) + random.nextInt(3) + 5);
    }

    @Override
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS || soil.getType() == Material.DIRT;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {

        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        float d = (float) (random.nextFloat() * Math.PI * 2.0F); // random direction
        int dx = (int) (Math.cos(d) + 1.5F) - 1;
        int dz = (int) (Math.sin(d) + 1.5F) - 1;
        if (Math.abs(dx) > 0 && Math.abs(dz) > 0) { // reduce possible directions to NESW
            if (random.nextBoolean()) {
                dx = 0;
            } else {
                dz = 0;
            }
        }
        int twistHeight = height - 1 - random.nextInt(4);
        int twistCount = random.nextInt(3) + 1;
        int centerX = blockX;
        int centerZ = blockZ;
        int trunkTopY = 0;
        // generates the trunk
        for (int y = 0; y < height; y++) {

            // trunk twists
            if (twistCount > 0 && y >= twistHeight) {
                centerX += dx;
                centerZ += dz;
                twistCount--;
            }

            Material material = blockTypeAt(centerX, blockY + y, centerZ, world);
            if (material == Material.AIR || material == Material.LEAVES) {
                trunkTopY = blockY + y;
                delegate.setTypeAndRawData(world, centerX, blockY + y, centerZ,
                    Material.LOG_2, 0);
            }
        }

        // generates leaves
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) < 3 || Math.abs(z) < 3) {
                    setLeaves(centerX + x, trunkTopY, centerZ + z, world);
                }
                if (Math.abs(x) < 2 && Math.abs(z) < 2) {
                    setLeaves(centerX + x, trunkTopY + 1, centerZ + z, world);
                }
                if (Math.abs(x) == 2 && Math.abs(z) == 0 || Math.abs(x) == 0 && Math.abs(z) == 2) {
                    setLeaves(centerX + x, trunkTopY + 1, centerZ + z, world);
                }
            }
        }

        // try to choose a different direction for second branching and canopy
        d = (float) (random.nextFloat() * Math.PI * 2.0F);
        int dxB = (int) (Math.cos(d) + 1.5F) - 1;
        int dzB = (int) (Math.sin(d) + 1.5F) - 1;
        if (Math.abs(dxB) > 0 && Math.abs(dzB) > 0) {
            if (random.nextBoolean()) {
                dxB = 0;
            } else {
                dzB = 0;
            }
        }
        if (dx != dxB || dz != dzB) {
            centerX = blockX;
            centerZ = blockZ;
            int branchHeight = twistHeight - 1 - random.nextInt(2);
            twistCount = random.nextInt(3) + 1;
            trunkTopY = 0;

            // generates the trunk
            for (int y = branchHeight + 1; y < height; y++) {
                if (twistCount > 0) {
                    centerX += dxB;
                    centerZ += dzB;
                    Material material = blockTypeAt(centerX, blockY + y, centerZ, world);
                    if (material == Material.AIR || material == Material.LEAVES) {
                        trunkTopY = blockY + y;
                        delegate.setTypeAndRawData(world, centerX, blockY + y,
                            centerZ, Material.LOG_2, 0);
                    }
                    twistCount--;
                }
            }

            // generates the leaves
            if (trunkTopY > 0) {
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        if (Math.abs(x) < 2 || Math.abs(z) < 2) {
                            setLeaves(centerX + x, trunkTopY, centerZ + z, world);
                        }
                    }
                }
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        setLeaves(centerX + x, trunkTopY + 1, centerZ + z, world);
                    }
                }
            }
        }

        // block below trunk is always dirt
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate.setTypeAndData(world, blockX, blockY - 1, blockZ, Material.DIRT, dirt);

        return true;
    }

    private void setLeaves(int x, int y, int z, World world) {
        if (blockTypeAt(x, y, z, world) == Material.AIR) {
            delegate.setTypeAndRawData(world, x, y, z, Material.LEAVES_2, 0);
        }
    }
}
