package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Vine;

public class CocoaTree extends JungleTree {

    private static final BlockFace[] COCOA_FACES = {BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST};
    private static final CocoaPlantSize[] COCOA_SIZE = {CocoaPlantSize.SMALL, CocoaPlantSize.MEDIUM,
        CocoaPlantSize.LARGE};

    /**
     * Initializes this tree, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     */
    public CocoaTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (!super.generate(world, random, blockX, blockY, blockZ)) {
            return false;
        }

        // places some vines on the trunk
        addVinesOnTrunk(blockX, blockY, blockZ, world, random);
        // search for air around leaves to grow hanging vines
        addVinesOnLeaves(blockX, blockY, blockZ, world, random);
        // and maybe place some cocoa
        addCocoa(blockX, blockY, blockZ, world, random);

        return true;
    }

    protected void addVinesOnLeaves(int baseX, int baseY, int baseZ, World world, Random random) {
        for (int y = baseY - 3 + height; y <= baseY + height; y++) {
            int ny = y - (baseY + height);
            int radius = 2 - ny / 2;
            for (int x = baseX - radius; x <= baseX + radius; x++) {
                for (int z = baseZ - radius; z <= baseZ + radius; z++) {
                    if (blockTypeAt(x, y, z, world)
                            == Material.LEAVES) {
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x - 1, y, z, world)
                                == Material.AIR) {
                            addHangingVine(x - 1, y, z, BlockFace.EAST, world);
                        }
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x + 1, y, z, world)
                                == Material.AIR) {
                            addHangingVine(x + 1, y, z, BlockFace.WEST, world);
                        }
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x, y, z - 1, world)
                                == Material.AIR) {
                            addHangingVine(x, y, z - 1, BlockFace.SOUTH, world);
                        }
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x, y, z + 1, world)
                                == Material.AIR) {
                            addHangingVine(x, y, z + 1, BlockFace.NORTH, world);
                        }
                    }
                }
            }
        }
    }

    private void addVinesOnTrunk(int trunkX, int trunkY, int trunkZ, World world, Random random) {
        for (int y = 1; y < height; y++) {
            if (random.nextInt(3) != 0
                    && blockTypeAt(trunkX - 1, trunkY + y, trunkZ, world)
                    == Material.AIR) {
                delegate.setTypeAndData(world, trunkX - 1, trunkY + y,
                        trunkZ, Material.VINE, new Vine(BlockFace.EAST));
            }
            if (random.nextInt(3) != 0
                    && blockTypeAt(trunkX + 1, trunkY + y, trunkZ, world)
                    == Material.AIR) {
                delegate.setTypeAndData(world, trunkX + 1, trunkY + y,
                        trunkZ, Material.VINE, new Vine(BlockFace.WEST));
            }
            if (random.nextInt(3) != 0
                    && blockTypeAt(trunkX, trunkY + y, trunkZ - 1, world)
                    == Material.AIR) {
                delegate.setTypeAndData(world, trunkX, trunkY + y,
                        trunkZ - 1, Material.VINE, new Vine(BlockFace.SOUTH));
            }
            if (random.nextInt(3) != 0
                    && blockTypeAt(trunkX, trunkY + y, trunkZ + 1, world)
                    == Material.AIR) {
                delegate.setTypeAndData(world, trunkX, trunkY + y,
                        trunkZ + 1, Material.VINE, new Vine(BlockFace.NORTH));
            }
        }
    }

    private void addHangingVine(int x, int y, int z, BlockFace face, World world) {
        for (int i = 0; i < 5; i++) {
            if (blockTypeAt(x, y - i, z, world) != Material.AIR) {
                break;
            }
            delegate.setTypeAndData(world, x, y - i, z, Material.VINE, new Vine(face));
        }
    }

    private void addCocoa(int sourceX, int sourceY, int sourceZ, World world, Random random) {
        if (height > 5 && random.nextInt(5) == 0) {
            for (int y = 0; y < 2; y++) {
                for (BlockFace cocoaFace : COCOA_FACES) { // rotate the 4 trunk faces
                    if (random.nextInt(COCOA_FACES.length - y)
                            == 0) { // higher it is, more chances there is
                        CocoaPlantSize size = COCOA_SIZE[random.nextInt(COCOA_SIZE.length)];
                        Block block = delegate
                                .getBlockState(world, sourceX, sourceY + height - 5 + y,
                                        sourceZ)
                                .getBlock().getRelative(cocoaFace);
                        delegate.setTypeAndData(world, block.getX(), block.getY(),
                                block.getZ(),
                                Material.COCOA, new CocoaPlant(size, cocoaFace.getOppositeFace()));
                    }
                }
            }
        }
    }
}
