package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

import java.util.Random;

public class DarkOakTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *  @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public DarkOakTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setOverridables(
            Material.AIR,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.GRASS,
            Material.DIRT,
            Material.LOG,
            Material.LOG_2,
            Material.SAPLING,
            Material.VINE
        );
        setHeight(random.nextInt(2) + random.nextInt(3) + 6);
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
        int twistHeight = height - random.nextInt(4);
        int twistCount = random.nextInt(3);
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
            if (material != Material.AIR && material != Material.LEAVES) {
                continue;
            }
            trunkTopY = blockY + y;
            // SELF, SOUTH, EAST, SOUTH EAST
            delegate.setTypeAndRawData(world, centerX, blockY + y, centerZ,
                Material.LOG_2, 1);
            delegate
                .setTypeAndRawData(world, centerX, blockY + y, centerZ + 1,
                    Material.LOG_2, 1);
            delegate
                .setTypeAndRawData(world, centerX + 1, blockY + y, centerZ,
                    Material.LOG_2, 1);
            delegate.setTypeAndRawData(world, centerX + 1, blockY + y,
                centerZ + 1, Material.LOG_2, 1);
        }

        // generates leaves
        for (int x = -2; x <= 0; x++) {
            for (int z = -2; z <= 0; z++) {
                if ((x != -1 || z != -2) && (x > -2 || z > -1)) {
                    setLeaves(centerX + x, trunkTopY + 1, centerZ + z, world);
                    setLeaves(1 + centerX - x, trunkTopY + 1, centerZ + z, world);
                    setLeaves(centerX + x, trunkTopY + 1, 1 + centerZ - z, world);
                    setLeaves(1 + centerX - x, trunkTopY + 1, 1 + centerZ - z, world);
                }
                setLeaves(centerX + x, trunkTopY - 1, centerZ + z, world);
                setLeaves(1 + centerX - x, trunkTopY - 1, centerZ + z, world);
                setLeaves(centerX + x, trunkTopY - 1, 1 + centerZ - z, world);
                setLeaves(1 + centerX - x, trunkTopY - 1, 1 + centerZ - z, world);
            }
        }

        // finish leaves below the canopy
        for (int x = -3; x <= 4; x++) {
            for (int z = -3; z <= 4; z++) {
                if (Math.abs(x) < 3 || Math.abs(z) < 3) {
                    setLeaves(centerX + x, trunkTopY, centerZ + z, world);
                }
            }
        }

        // generates some trunk excrescences
        for (int x = -1; x <= 2; x++) {
            for (int z = -1; z <= 2; z++) {
                if ((x != -1 && z != -1 && x != 2 && z != 2) || random.nextInt(3) != 0) {
                    continue;
                }
                for (int y = 0; y < random.nextInt(3) + 2; y++) {
                    Material material = blockTypeAt(
                            blockX + x, trunkTopY - y - 1, blockZ + z, world);
                    if (material == Material.AIR || material == Material.LEAVES) {
                        delegate.setTypeAndRawData(world, blockX + x,
                            trunkTopY - y - 1, blockZ + z, Material.LOG_2, 1);
                    }
                }

                // leaves below the canopy
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        setLeaves(centerX + x + i, trunkTopY, centerZ + z + j, world);
                    }
                }
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (Math.abs(i) < 2 || Math.abs(j) < 2) {
                            setLeaves(centerX + x + i, trunkTopY - 1, centerZ + z + j, world);
                        }
                    }
                }
            }
        }

        // 50% chance to have a 4 leaves cap on the center of the canopy
        if (random.nextInt(2) == 0) {
            setLeaves(centerX, trunkTopY + 2, centerZ, world);
            setLeaves(centerX + 1, trunkTopY + 2, centerZ, world);
            setLeaves(centerX + 1, trunkTopY + 2, centerZ + 1, world);
            setLeaves(centerX, trunkTopY + 2, centerZ + 1, world);
        }

        // block below trunk is always dirt (SELF, SOUTH, EAST, SOUTH EAST)
        Dirt dirt = new Dirt(DirtType.NORMAL);
        delegate
            .setTypeAndData(world, blockX, blockY - 1, blockZ,
                Material.DIRT, dirt);
        delegate.setTypeAndData(world, blockX, blockY - 1,
            blockZ + 1, Material.DIRT, dirt);
        delegate.setTypeAndData(world, blockX + 1, blockY - 1,
            blockZ, Material.DIRT, dirt);
        delegate.setTypeAndData(world, blockX + 1, blockY - 1,
            blockZ + 1, Material.DIRT, dirt);

        return true;
    }

    private void setLeaves(int x, int y, int z, World world) {
        if (blockTypeAt(x, y, z, world) == Material.AIR) {
            delegate.setTypeAndRawData(world, x, y, z, Material.LEAVES_2, 1);
        }
    }
}
