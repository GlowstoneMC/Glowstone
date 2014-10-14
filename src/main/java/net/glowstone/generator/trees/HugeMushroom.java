package net.glowstone.generator.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.Random;

public class HugeMushroom extends GenericTree {
    private Material type;

    public HugeMushroom(Random random, Material type, BlockStateDelegate delegate) {
        super(random, delegate);
        if (type != Material.HUGE_MUSHROOM_1 && type != Material.HUGE_MUSHROOM_2) {
            throw new IllegalArgumentException("Invalid huge mushroom type");
        }
        this.type = type;
        setOverridables(
                Material.AIR,
                Material.LEAVES,
                Material.LEAVES_2
        );
        setHeight(random.nextInt(3) + 4);
    }

    @Override
    public boolean canPlaceOn(World world, int x, int y, int z) {
        final BlockState state = delegate.getBlockState(world, x, y, z);
        return state.getType() == Material.GRASS ||
                state.getType() == Material.DIRT ||
                state.getType() == Material.MYCEL;
    }

    @Override
    public boolean canPlaceAt(World world, int sourceX, int sourceY, int sourceZ) {
        for (int y = sourceY; y <= sourceY + 1 + height; y++) {
            // Space requirement is 7x7 blocks, so brown mushroom's cap
            // can be directly touching a mushroom next to it.
            // Since red mushrooms fits in 5x5 blocks it will never
            // touch another huge mushroom.
            int radius = 3;
            if (y <= sourceY + 3) {
                radius = 0; // radius is 0 below 4 blocks tall (only the stem to take in account)
            }

            // check for block collision on horizontal slices
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // skip source block check
                        if (y != sourceY || x != sourceX || z != sourceZ) {
                            // we can overlap leaves around
                            final Material type = delegate.getBlockState(world, x, y, z).getType();
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
    public boolean generate(World world, int sourceX, int sourceY, int sourceZ) {
        // check height range
        if (!canHeightFitAt(sourceY)) {
            return false;
        }

        // check below block
        if (!canPlaceOn(world, sourceX, sourceY - 1, sourceZ)) {
            return false;
        }

        // check for sufficient space around
        if (!canPlaceAt(world, sourceX, sourceY, sourceZ)) {
            return false;
        }

        // generate the stem
        for (int y = 0; y < height; y++) {
            delegate.setTypeAndRawData(world, sourceX, sourceY + y, sourceZ, type, 10); // stem texture
        }

        // get the mushroom's cap Y start
        int capY = sourceY + height; // for brown mushroom it starts on top directly
        if (type == Material.HUGE_MUSHROOM_2) {
            capY = sourceY + height - 3; // for red mushroom, cap's thickness is 4 blocks
        }

        // generate mushroom's cap
        for (int y = capY; y <= sourceY + height; y++) { // from bottom to top of mushroom
            int radius = 1; // radius for the top of red mushroom
            if (y < sourceY + height) {
                radius = 2; // radius for red mushroom cap is 2
            }
            if (type == Material.HUGE_MUSHROOM_1) {
                radius = 3; // radius always 3 for a brown mushroom
            }
            // loop over horizontal slice
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    int data = 5; // cap texture on top
                    // cap's borders/corners treatment
                    if (x == sourceX - radius) {
                        data = 4; // cap texture on top and west
                    } else if (x == sourceX + radius) {
                        data = 6; // cap texture on top and east
                    }
                    if (z == sourceZ - radius) {
                        data -= 3;
                    } else if (z == sourceZ + radius) {
                        data += 3;
                    }

                    // corners shrink treatment
                    // if it's a brown mushroom we need it always
                    // it's a red mushroom, it's only applied below the top
                    if (type == Material.HUGE_MUSHROOM_1 || y < sourceY + height) {

                        // excludes the real corners of the cap structure
                        if ((x == sourceX - radius || x == sourceX + radius)
                                && (z == sourceZ - radius || z == sourceZ + radius)) {
                            continue;
                        }

                        // mushroom's cap corners treatment
                        if (x == sourceX - (radius - 1) && z == sourceZ - radius) {
                            data = 1; // cap texture on top, west and north
                        } else if (x == sourceX - radius && z == sourceZ - (radius - 1)) {
                            data = 1; // cap texture on top, west and north
                        } else if (x == sourceX + (radius - 1) && z == sourceZ - radius) {
                            data = 3; // cap texture on top, north and east
                        } else if (x == sourceX + radius && z == sourceZ - (radius - 1)) {
                            data = 3; // cap texture on top, north and east
                        } else if (x == sourceX - (radius - 1) && z == sourceZ + radius) {
                            data = 7; // cap texture on top, south and west
                        } else if (x == sourceX - radius && z == sourceZ + (radius - 1)) {
                            data = 7; // cap texture on top, south and west
                        } else if (x == sourceX + (radius - 1) && z == sourceZ + radius) {
                            data = 9; // cap texture on top, east and south
                        } else if (x == sourceX + radius && z == sourceZ + (radius - 1)) {
                            data = 9; // cap texture on top, east and south
                        }
                    }

                    // a data of 5 below the top layer means air
                    if (data != 5 || y >= sourceY + height) {
                        delegate.setTypeAndRawData(world, x, y, z, type, data);
                    }
                }
            }
        }

        return true;
    }
}
