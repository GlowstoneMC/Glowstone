package net.glowstone.util.pattern;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * A PortalShape functions as a validator for Nether Portals.
 */
public class PortalShape {

    private final BlockFace left;
    private int portalBlockCount;
    private Location bottomLeft;
    @Getter
    private int height;
    @Getter
    private int width;

    public PortalShape(Location buildLocation, BlockFace portalFace) {
        if (portalFace == BlockFace.WEST || portalFace == BlockFace.EAST) {
            left = BlockFace.WEST;
        } else if (portalFace == BlockFace.NORTH || portalFace == BlockFace.SOUTH) {
            left = BlockFace.NORTH;
        } else {
            throw new IllegalArgumentException(
                    "Invalid Blockface: " + portalFace
                            + ". Supported are only NORTH, SOUTH, EAST, WEST");
        }

        //Locations are mutable so we have to clone to compare y-values
        Location location = buildLocation.clone();

        // calculations start on the lower part, so we have to move down
        while (location.getY() > buildLocation.getY() - 21 && location.getY() > 0 // validate y axis
                && canBuildThrough(downImmutable(location).getBlock().getType())) {
            // move downwards
            location.subtract(0, 1, 0);
        }


        // No obsidian in 23 block range
        if (downImmutable(location).getBlock().getType() != Material.OBSIDIAN) {
            return;
        }

        // get distance to leftmost edge
        int i = getDistanceUntilEdge(location, left);

        // No edge -> no portal
        if (i < 0) {
            return;
        }

        // Set the bottom leftmost portal bloc
        bottomLeft = offsetImmutable(location, left, i);

        // Calculate the portal width
        width = getDistanceUntilEdge(bottomLeft, left.getOppositeFace()) + 1;

        // Portal too big / too small
        if (width < 2 || width > 21) {
            return;
        }

        // calculate portal height
        height = calculatePortalHeight();
    }

    /**
     * Get the distance to one edge of the possible portal. Returns -1 if no end is reached.
     * @param location the location from where to check. Not modified in process.
     * @param face the side of the edge to check for
     * @return the distance or -1 in case of an invalid shape
     */
    private int getDistanceUntilEdge(Location location, BlockFace face) {
        // Clone the location as Location objects are mutable
        Location destLoc = location.clone();

        int i;
        for (i = 0; i < 22; ++i) {
            // Move onwards
            destLoc.add(face.getModX(), face.getModY(), face.getModZ());
            // Break if either an obstacle or the end of the obsidian "line" is reached
            if (!canBuildThrough(destLoc.getBlock().getType())
                    || downImmutable(destLoc).getBlock().getType() != Material.OBSIDIAN) {
                break;
            }
        }
        // If the target block is not obsidian, the portal is invalid.
        return destLoc.getBlock().getType() == Material.OBSIDIAN ? i : -1;
    }

    private int calculatePortalHeight() {
        for (height = 0; height < 21; ++height) {
            boolean flag = false;
            for (int i = 0; i < width; ++i) {
                Location location = offsetImmutable(bottomLeft, left.getOppositeFace(), i)
                        .add(0, height, 0);
                Material material = location.getBlock().getType();

                if (!canBuildThrough(material)) {
                    flag = true;
                    break;
                }

                if (material == Material.PORTAL)  {
                    ++portalBlockCount;
                }

                if (i == 0) {
                    material = offsetImmutable(location, left, 1).getBlock().getType();
                    if (material != Material.OBSIDIAN) {
                        flag = true;
                        break;
                    }
                } else if (i == width - 1) {
                    material = offsetImmutable(location, left, -1).getBlock().getType();
                    if (material != Material.OBSIDIAN) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                break;
            }
        }

        for (int j = 0; j < this.width; ++j) {
            if (offsetImmutable(bottomLeft, left, -j).add(0, height, 0).getBlock().getType()
                    != Material.OBSIDIAN) {
                height = 0;
                break;
            }
        }

        if (height <= 21 && height >= 3) {
            return height;
        } else {
            bottomLeft = null;
            width = 0;
            height = 0;
            return 0;
        }
    }

    /**
     * Check whether a given Material counts as empty in a portal.
     * @param material the material to check for
     * @return whether the material counts as empty
     */
    private static boolean canBuildThrough(Material material) {
        return material == Material.AIR || material == Material.FIRE || material == Material.PORTAL;
    }

    /**
     * Validate the portal shape.
     * @return whether the portal shape is valid
     */
    public boolean validate() {
        return bottomLeft != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
    }

    public void placePortalBlocks() {
        for (int i = 0; i < width; ++i) {
            Location blockpos = offsetImmutable(bottomLeft, left.getOppositeFace(), i);

            for (int j = 0; j < this.height; ++j) {
                blockpos.add(0, j, 0).getBlock().setType(Material.PORTAL);
            }
        }
    }

    private static Location downImmutable(Location in) {
        return new Location(in.getWorld(), in.getX(),in.getY() - 1, in.getZ());
    }

    private static Location offsetImmutable(Location in, BlockFace face, int offset) {
        return new Location(
                in.getWorld(),
                in.getX() + (face.getModX() * offset),
                in.getY() + (face.getModY() * offset),
                in.getZ() + (face.getModZ() * offset)
        );
    }
}
