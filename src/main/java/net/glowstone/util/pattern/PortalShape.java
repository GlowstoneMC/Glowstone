package net.glowstone.util.pattern;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.glowstone.EventFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.world.PortalCreateEvent;

/**
 * A PortalShape functions as a validator for Nether Portals.
 */
public class PortalShape {

    /**
     * Max portal width or height.
     */
    private static final int MAX_PORTAL_WIDTH_HEIGHT = 21;

    /**
     * Min portal height.
     */
    private static final int MIN_PORTAL_HEIGHT = 3;

    /**
     * Min portal width.
     */
    private static final int MIN_PORTAL_WIDTH = 2;

    /**
     * The direction that is considered left.
     */
    private final BlockFace left;
    /**
     * All blocks that already are portal blocks.
     */
    @Getter
    private int portalBlockCount;
    /**
     * The bottom leftmost location.
     */
    private Location bottomLeft;
    /**
     * The height oft this portal.
     */
    @Getter
    private int height;
    /**
     * The width of this portal.
     */
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
        while (location.getY() > buildLocation.getY() - MAX_PORTAL_WIDTH_HEIGHT
            && location.getY() > 0 && canBuildThrough(downImmutable(location).getBlock().getType())) {
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
        if (width < MIN_PORTAL_WIDTH || width > MAX_PORTAL_WIDTH_HEIGHT) {
            return;
        }

        // calculate portal height
        height = calculatePortalHeight();
    }

    /**
     * Check whether a given Material counts as empty in a portal.
     *
     * @param material the material to check for
     * @return whether the material counts as empty
     */
    private static boolean canBuildThrough(Material material) {
        return material == Material.AIR || material == Material.FIRE || material == Material.PORTAL;
    }

    /**
     * Go down 1 block from a Location without changing the Location itself.
     * Needed to perform a "look-ahead" while preserving the original location.
     *
     * @param in the location to go down from
     * @return a new Location with the y-value decreased by one
     */
    private static Location downImmutable(Location in) {
        return new Location(in.getWorld(), in.getX(), in.getY() - 1, in.getZ());
    }

    /**
     * Offset from a given location in a certain direction by a specific amount of times.
     * Needed to preserve the original location while offsetting.
     *
     * @param in     the original location to offset from
     * @param face   the direction in which to offset
     * @param offset how many times to offset
     * @return a new Location with the specified offset
     */
    private static Location offsetImmutable(Location in, BlockFace face, int offset) {
        return new Location(
            in.getWorld(),
            in.getX() + (face.getModX() * offset),
            in.getY() + (face.getModY() * offset),
            in.getZ() + (face.getModZ() * offset)
        );
    }

    /**
     * Get the distance to one edge of the possible portal. Returns -1 if no end is reached.
     *
     * @param location the location from where to check. Not modified in process.
     * @param face     the side of the edge to check for
     * @return the distance or -1 in case of an invalid shape
     */
    private int getDistanceUntilEdge(Location location, BlockFace face) {
        // Clone the location as Location objects are mutable
        Location destLoc = location.clone();

        int distance;
        for (distance = 0; distance <= MAX_PORTAL_WIDTH_HEIGHT; ++distance) {
            // Move onwards
            destLoc.add(face.getModX(), face.getModY(), face.getModZ());
            // Break if either an obstacle or the end of the obsidian "line" is reached
            if (!canBuildThrough(destLoc.getBlock().getType())
                || downImmutable(destLoc).getBlock().getType() != Material.OBSIDIAN) {
                break;
            }
        }
        // If the target block is not obsidian, the portal is invalid.
        return destLoc.getBlock().getType() == Material.OBSIDIAN ? distance : -1;
    }

    /**
     * Calculate the portals height.
     *
     * @return the height or 0 in case of failure
     */
    private int calculatePortalHeight() {
        for (height = 0; height < MAX_PORTAL_WIDTH_HEIGHT; ++height) {
            boolean flag = false;
            for (int i = 0; i < width; ++i) {
                Location location = offsetImmutable(bottomLeft, left.getOppositeFace(), i)
                    .add(0, height, 0);
                Material material = location.getBlock().getType();

                if (!canBuildThrough(material)) {
                    flag = true;
                    break;
                }

                if (material == Material.PORTAL) {
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

        for (int j = 0; j < width; ++j) {
            if (offsetImmutable(bottomLeft, left, -j).add(0, height, 0).getBlock().getType()
                != Material.OBSIDIAN) {
                height = 0;
                break;
            }
        }

        if (height <= MAX_PORTAL_WIDTH_HEIGHT && height >= MIN_PORTAL_HEIGHT) {
            return height;
        }

        bottomLeft = null;
        width = 0;
        height = 0;
        return 0;

    }

    /**
     * Validate the portal shape.
     *
     * @return whether the portal shape is valid
     */
    public boolean validate() {
        return bottomLeft != null && width >= MIN_PORTAL_WIDTH && width <= MAX_PORTAL_WIDTH_HEIGHT
            && height >= MIN_PORTAL_HEIGHT && height <= MAX_PORTAL_WIDTH_HEIGHT;
    }

    /**
     * Place the portal blocks.
     */
    @SuppressWarnings("deprecation")
    public void placePortalBlocks() {
        List<Block> portalBlocks = new ArrayList<>(6);
        for (int i = 0; i < width; ++i) {
            //we have to go down 1 block as Locations are mutable.
            Location loc = offsetImmutable(bottomLeft, left.getOppositeFace(), i).subtract(0, 1, 0);
            for (int j = 0; j < height; ++j) {
                portalBlocks.add(loc.add(0, 1, 0).getBlock());
            }
        }
        PortalCreateEvent event = new PortalCreateEvent(portalBlocks, bottomLeft.getWorld(),
            PortalCreateEvent.CreateReason.FIRE);
        if (!EventFactory.getInstance().callEvent(event).isCancelled()) {
            // Dirty hack: directly calculate the metadata; Bukkit does not have portal material data :/
            byte meta = (byte) (left == BlockFace.WEST ? 1 : 2);
            event.getBlocks().forEach(block -> {
                block.setType(Material.PORTAL);
                block.setData(meta);
            });
        }
    }
}
