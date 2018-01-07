package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
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
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and
     *         leaf blocks
     */
    public CocoaTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
    }

    @Override
    public boolean generate() {
        if (!super.generate()) {
            return false;
        }

        // places some vines on the trunk
        addVinesOnTrunk();
        // search for air around leaves to grow hanging vines
        addVinesOnLeaves();
        // and maybe place some cocoa
        addCocoa(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        return true;
    }

    protected void addVinesOnLeaves() {
        for (int y = loc.getBlockY() - 3 + height; y <= loc.getBlockY() + height; y++) {
            int ny = y - (loc.getBlockY() + height);
            int radius = 2 - ny / 2;
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (blockTypeAt(x, y, z)
                            == Material.LEAVES) {
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x - 1, y, z)
                                == Material.AIR) {
                            addHangingVine(x - 1, y, z, BlockFace.EAST);
                        }
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x + 1, y, z)
                                == Material.AIR) {
                            addHangingVine(x + 1, y, z, BlockFace.WEST);
                        }
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x, y, z - 1)
                                == Material.AIR) {
                            addHangingVine(x, y, z - 1, BlockFace.SOUTH);
                        }
                        if (random.nextInt(4) == 0
                                && blockTypeAt(x, y, z + 1)
                                == Material.AIR) {
                            addHangingVine(x, y, z + 1, BlockFace.NORTH);
                        }
                    }
                }
            }
        }
    }

    private void addVinesOnTrunk() {
        for (int y = 1; y < height; y++) {
            if (random.nextInt(3) != 0
                    && blockTypeAt(loc.getBlockX() - 1, loc.getBlockY() + y, loc.getBlockZ())
                    == Material.AIR) {
                delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() - 1, loc.getBlockY() + y,
                        loc.getBlockZ(), Material.VINE, new Vine(BlockFace.EAST));
            }
            if (random.nextInt(3) != 0
                    && blockTypeAt(loc.getBlockX() + 1, loc.getBlockY() + y, loc.getBlockZ())
                    == Material.AIR) {
                delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() + y,
                        loc.getBlockZ(), Material.VINE, new Vine(BlockFace.WEST));
            }
            if (random.nextInt(3) != 0
                    && blockTypeAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ() - 1)
                    == Material.AIR) {
                delegate.setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y,
                        loc.getBlockZ() - 1, Material.VINE, new Vine(BlockFace.SOUTH));
            }
            if (random.nextInt(3) != 0
                    && blockTypeAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ() + 1)
                    == Material.AIR) {
                delegate.setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y,
                        loc.getBlockZ() + 1, Material.VINE, new Vine(BlockFace.NORTH));
            }
        }
    }

    private void addHangingVine(int x, int y, int z, BlockFace face) {
        for (int i = 0; i < 5; i++) {
            if (blockTypeAt(x, y - i, z) != Material.AIR) {
                break;
            }
            delegate.setTypeAndData(loc.getWorld(), x, y - i, z, Material.VINE, new Vine(face));
        }
    }

    private void addCocoa(int sourceX, int sourceY, int sourceZ) {
        if (height > 5 && random.nextInt(5) == 0) {
            for (int y = 0; y < 2; y++) {
                for (BlockFace cocoaFace : COCOA_FACES) { // rotate the 4 trunk faces
                    if (random.nextInt(COCOA_FACES.length - y)
                            == 0) { // higher it is, more chances there is
                        CocoaPlantSize size = COCOA_SIZE[random.nextInt(COCOA_SIZE.length)];
                        Block block = delegate
                                .getBlockState(loc.getWorld(), sourceX, sourceY + height - 5 + y,
                                        sourceZ)
                                .getBlock().getRelative(cocoaFace);
                        delegate.setTypeAndData(loc.getWorld(), block.getX(), block.getY(),
                                block.getZ(),
                                Material.COCOA, new CocoaPlant(size, cocoaFace.getOppositeFace()));
                    }
                }
            }
        }
    }
}
