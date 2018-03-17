package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

public class MegaPineTree extends MegaRedwoodTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
     */
    public MegaPineTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setLeavesHeight(random.nextInt(5) + 3);
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        boolean generated = super.generate(world, random, blockX, blockY, blockZ);
        if (generated) {
            generatePodzol(blockX, blockY, blockZ, world, random);
        }
        return generated;
    }

    @Override
    protected void generateDirtBelowTrunk(World world, int blockX, int blockY,
            int blockZ) {
        // SELF, SOUTH, EAST, SOUTH EAST
        Dirt dirt = new Dirt(DirtType.PODZOL);
        delegate
            .setTypeAndData(world, blockX, blockY - 1, blockZ,
                Material.DIRT, dirt);
        delegate.setTypeAndData(world, blockX, blockY - 1,
            blockZ + 1, Material.DIRT, dirt);
        delegate.setTypeAndData(world, blockX + 1, blockY - 1,
            blockZ, Material.DIRT, dirt);
        delegate.setTypeAndData(world, blockX + 1, blockY - 1,
            blockZ + 1, Material.DIRT, dirt);
    }

    private void generatePodzol(int sourceX, int sourceY, int sourceZ, World world, Random random) {
        generatePodzolPatch(sourceX - 1, sourceY, sourceZ - 1, world);
        generatePodzolPatch(sourceX + 2, sourceY, sourceZ - 1, world);
        generatePodzolPatch(sourceX - 1, sourceY, sourceZ + 2, world);
        generatePodzolPatch(sourceX + 2, sourceY, sourceZ + 2, world);
        for (int i = 0; i < 5; i++) {
            int n = random.nextInt(64);
            if (n % 8 == 0 || n % 8 == 7 || n / 8 == 0 || n / 8 == 7) {
                generatePodzolPatch(sourceX - 3 + n % 8, sourceY, sourceZ - 3 + n / 8, world);
            }
        }
    }

    private void generatePodzolPatch(int sourceX, int sourceY, int sourceZ, World world) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) != 2 || Math.abs(z) != 2) {
                    for (int y = 2; y >= -3; y--) {
                        Block block = world
                            .getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        if (block.getType() == Material.GRASS || block.getType() == Material.DIRT) {
                            BlockState state = block.getState();
                            state.setType(Material.DIRT);
                            DirtType dirtType = DirtType.PODZOL;
                            if (world.getBlockAt(sourceX + x, sourceY + y + 1, sourceZ + z)
                                .getType().isOccluding()) {
                                dirtType = DirtType.NORMAL;
                            }
                            state.setData(new Dirt(dirtType));
                            state.update(true);
                        } else if (!block.isEmpty() && sourceY + y < sourceY) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
