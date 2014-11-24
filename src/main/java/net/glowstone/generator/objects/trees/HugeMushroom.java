package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.Random;

public class HugeMushroom extends GenericTree {
    private Material type;

    public HugeMushroom(Random random, Location location, Material type, BlockStateDelegate delegate) {
        super(random, location, delegate);
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
    public boolean canPlaceOn() {
        final BlockState state = delegate.getBlockState(loc.getBlock().getRelative(BlockFace.DOWN).getLocation());
        return state.getType() == Material.GRASS || state.getType() == Material.DIRT || state.getType() == Material.MYCEL;
    }

    @Override
    public boolean canPlace() {
        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 1 + height; y++) {
            // Space requirement is 7x7 blocks, so brown mushroom's cap
            // can be directly touching a mushroom next to it.
            // Since red mushrooms fits in 5x5 blocks it will never
            // touch another huge mushroom.
            int radius = 3;
            if (y <= loc.getBlockY() + 3) {
                radius = 0; // radius is 0 below 4 blocks tall (only the stem to take in account)
            }

            // check for block collision on horizontal slices
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    if (y >= 0 && y < 256) {
                        // skip source block check
                        if (y != loc.getBlockY() || x != loc.getBlockX() || z != loc.getBlockZ()) {
                            // we can overlap leaves around
                            final Material type = delegate.getBlockState(loc.getWorld(), x, y, z).getType();
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
    public boolean generate() {
        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        // generate the stem
        for (int y = 0; y < height; y++) {
            delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ(), type, 10); // stem texture
        }

        // get the mushroom's cap Y start
        int capY = loc.getBlockY() + height; // for brown mushroom it starts on top directly
        if (type == Material.HUGE_MUSHROOM_2) {
            capY = loc.getBlockY() + height - 3; // for red mushroom, cap's thickness is 4 blocks
        }

        // generate mushroom's cap
        for (int y = capY; y <= loc.getBlockY() + height; y++) { // from bottom to top of mushroom
            int radius = 1; // radius for the top of red mushroom
            if (y < loc.getBlockY() + height) {
                radius = 2; // radius for red mushroom cap is 2
            }
            if (type == Material.HUGE_MUSHROOM_1) {
                radius = 3; // radius always 3 for a brown mushroom
            }
            // loop over horizontal slice
            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    int data = 5; // cap texture on top
                    // cap's borders/corners treatment
                    if (x == loc.getBlockX() - radius) {
                        data = 4; // cap texture on top and west
                    } else if (x == loc.getBlockX() + radius) {
                        data = 6; // cap texture on top and east
                    }
                    if (z == loc.getBlockZ() - radius) {
                        data -= 3;
                    } else if (z == loc.getBlockZ() + radius) {
                        data += 3;
                    }

                    // corners shrink treatment
                    // if it's a brown mushroom we need it always
                    // it's a red mushroom, it's only applied below the top
                    if (type == Material.HUGE_MUSHROOM_1 || y < loc.getBlockY() + height) {

                        // excludes the real corners of the cap structure
                        if ((x == loc.getBlockX() - radius || x == loc.getBlockX() + radius)
                                && (z == loc.getBlockZ() - radius || z == loc.getBlockZ() + radius)) {
                            continue;
                        }

                        // mushroom's cap corners treatment
                        if (x == loc.getBlockX() - (radius - 1) && z == loc.getBlockZ() - radius) {
                            data = 1; // cap texture on top, west and north
                        } else if (x == loc.getBlockX() - radius && z == loc.getBlockZ() - (radius - 1)) {
                            data = 1; // cap texture on top, west and north
                        } else if (x == loc.getBlockX() + (radius - 1) && z == loc.getBlockZ() - radius) {
                            data = 3; // cap texture on top, north and east
                        } else if (x == loc.getBlockX() + radius && z == loc.getBlockZ() - (radius - 1)) {
                            data = 3; // cap texture on top, north and east
                        } else if (x == loc.getBlockX() - (radius - 1) && z == loc.getBlockZ() + radius) {
                            data = 7; // cap texture on top, south and west
                        } else if (x == loc.getBlockX() - radius && z == loc.getBlockZ() + (radius - 1)) {
                            data = 7; // cap texture on top, south and west
                        } else if (x == loc.getBlockX() + (radius - 1) && z == loc.getBlockZ() + radius) {
                            data = 9; // cap texture on top, east and south
                        } else if (x == loc.getBlockX() + radius && z == loc.getBlockZ() + (radius - 1)) {
                            data = 9; // cap texture on top, east and south
                        }
                    }

                    // a data of 5 below the top layer means air
                    if (data != 5 || y >= loc.getBlockY() + height) {
                        delegate.setTypeAndRawData(loc.getWorld(), x, y, z, type, data);
                    }
                }
            }
        }

        return true;
    }
}
