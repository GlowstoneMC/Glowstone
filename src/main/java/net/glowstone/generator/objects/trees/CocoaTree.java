package net.glowstone.generator.objects.trees;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.WEST;

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

    private static final BlockFace[] COCOA_FACES = {NORTH, EAST,
        SOUTH, WEST};
    private static final CocoaPlantSize[] COCOA_SIZE = {CocoaPlantSize.SMALL, CocoaPlantSize.MEDIUM,
        CocoaPlantSize.LARGE};

    /**
     * Initializes this tree, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
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
                    if (blockAt(x, y, z)
                        == Material.LEAVES) {
                        if (random.nextInt(4) == 0
                            && blockAt(x - 1, y, z)
                            == Material.AIR) {
                            addHangingVine(x - 1, y, z, EAST);
                        }
                        if (random.nextInt(4) == 0
                            && blockAt(x + 1, y, z)
                            == Material.AIR) {
                            addHangingVine(x + 1, y, z, WEST);
                        }
                        if (random.nextInt(4) == 0
                            && blockAt(x, y, z - 1)
                            == Material.AIR) {
                            addHangingVine(x, y, z - 1, SOUTH);
                        }
                        if (random.nextInt(4) == 0
                            && blockAt(x, y, z + 1)
                            == Material.AIR) {
                            addHangingVine(x, y, z + 1, NORTH);
                        }
                    }
                }
            }
        }
    }

    private void addVinesOnTrunk() {
        for (int y = 1; y < height; y++) {
            maybePlaceVine(-1, y, 0, EAST);
            maybePlaceVine(1, y, 0, WEST);
            maybePlaceVine(0, y, -1, SOUTH);
            maybePlaceVine(0, y, 1, NORTH);
        }
    }

    private void addHangingVine(int x, int y, int z, BlockFace face) {
        for (int i = 0; i < 5; i++) {
            if (blockAt(x, y - i, z) != Material.AIR) {
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
