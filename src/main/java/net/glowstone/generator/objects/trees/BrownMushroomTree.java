package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

public class BrownMushroomTree extends GenericTree {

    protected Material type;

    /**
     * Initializes this mushroom with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public BrownMushroomTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        type = Material.HUGE_MUSHROOM_1;
        setOverridables(
            Material.AIR,
            Material.LEAVES,
            Material.LEAVES_2
        );
        setHeight(random.nextInt(3) + 4);
    }

    @Override
    public boolean canPlaceOn(BlockState soil) {
        return soil.getType() == Material.GRASS || soil.getType() == Material.DIRT
            || soil.getType() == Material.MYCEL;
    }

    @Override
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        for (int y = baseY; y <= baseY + 1 + height; y++) {
            // Space requirement is 7x7 blocks, so brown mushroom's cap
            // can be directly touching a mushroom next to it.
            // Since red mushrooms fits in 5x5 blocks it will never
            // touch another huge mushroom.
            int radius = 3;
            if (y <= baseY + 3) {
                radius = 0; // radius is 0 below 4 blocks tall (only the stem to take in account)
            }

            // check for block collision on horizontal slices
            for (int x = baseX - radius; x <= baseX + radius; x++) {
                for (int z = baseZ - radius; z <= baseZ + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // skip source block check
                        if (y != baseY || x != baseX || z != baseZ) {
                            // we can overlap leaves around
                            Material type = blockTypeAt(x, y, z, world);
                            if (!overridables.contains(type)) {
                                return false;
                            }
                        }
                    } else { // height out of range
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        // generate the stem
        for (int y = 0; y < height; y++) {
            delegate.setTypeAndRawData(world, blockX, blockY + y,
                blockZ, type, 10); // stem texture
        }

        // get the mushroom's cap Y start
        int capY = blockY + height; // for brown mushroom it starts on top directly
        if (type == Material.HUGE_MUSHROOM_2) {
            capY = blockY + height - 3; // for red mushroom, cap's thickness is 4 blocks
        }

        // generate mushroom's cap
        for (int y = capY; y <= blockY + height; y++) { // from bottom to top of mushroom
            int radius = 1; // radius for the top of red mushroom
            if (y < blockY + height) {
                radius = 2; // radius for red mushroom cap is 2
            }
            if (type == Material.HUGE_MUSHROOM_1) {
                radius = 3; // radius always 3 for a brown mushroom
            }
            // loop over horizontal slice
            for (int x = blockX - radius; x <= blockX + radius; x++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    int data = 5; // cap texture on top
                    // cap's borders/corners treatment
                    if (x == blockX - radius) {
                        data = 4; // cap texture on top and west
                    } else if (x == blockX + radius) {
                        data = 6; // cap texture on top and east
                    }
                    if (z == blockZ - radius) {
                        data -= 3;
                    } else if (z == blockZ + radius) {
                        data += 3;
                    }

                    // corners shrink treatment
                    // if it's a brown mushroom we need it always
                    // it's a red mushroom, it's only applied below the top
                    if (type == Material.HUGE_MUSHROOM_1 || y < blockY + height) {

                        // excludes the real corners of the cap structure
                        if ((x == blockX - radius || x == blockX + radius)
                            && (z == blockZ - radius || z == blockZ + radius)) {
                            continue;
                        }

                        // mushroom's cap corners treatment
                        if (x == blockX - (radius - 1) && z == blockZ - radius) {
                            data = 1; // cap texture on top, west and north
                        } else if (x == blockX - radius && z == blockZ - (radius
                            - 1)) {
                            data = 1; // cap texture on top, west and north
                        } else if (x == blockX + radius - 1
                            && z == blockZ - radius) {
                            data = 3; // cap texture on top, north and east
                        } else if (x == blockX + radius && z == blockZ - (radius
                            - 1)) {
                            data = 3; // cap texture on top, north and east
                        } else if (x == blockX - (radius - 1)
                            && z == blockZ + radius) {
                            data = 7; // cap texture on top, south and west
                        } else if (x == blockX - radius
                            && z == blockZ + radius - 1) {
                            data = 7; // cap texture on top, south and west
                        } else if (x == blockX + radius - 1
                            && z == blockZ + radius) {
                            data = 9; // cap texture on top, east and south
                        } else if (x == blockX + radius
                            && z == blockZ + radius - 1) {
                            data = 9; // cap texture on top, east and south
                        }
                    }

                    // a data of 5 below the top layer means air
                    if (data != 5 || y >= blockY + height) {
                        delegate.setTypeAndRawData(world, x, y, z, type, data);
                    }
                }
            }
        }

        return true;
    }
}
