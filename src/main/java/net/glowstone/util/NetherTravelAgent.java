package net.glowstone.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class NetherTravelAgent implements TravelAgent {

    private int searchRadius = 128;
    private int creationRadius = 16;
    private boolean canCreate = true;
    private World world;

    public NetherTravelAgent(World world) {
        if (world.getEnvironment() != World.Environment.NETHER) {
            throw new IllegalArgumentException("World " + world.getName() + " is not a nether world.");
        }
        this.world = world;
    }

    @Override
    public TravelAgent setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
        return this;
    }

    @Override
    public int getSearchRadius() {
        return searchRadius;
    }

    @Override
    public TravelAgent setCreationRadius(int i) {
        creationRadius = i;
        return this;
    }

    @Override
    public int getCreationRadius() {
        return creationRadius;
    }

    @Override
    public boolean getCanCreatePortal() {
        return canCreate;
    }

    @Override
    public void setCanCreatePortal(boolean b) {
        canCreate = b;
    }


    @Override
    public Location findPortal(Location destination) {
        Location minLoc = null;
        double minDistance = Double.MAX_VALUE;

        final int blockX = destination.getBlockX();
        final int blockZ = destination.getBlockZ();

        for (int x = blockX - searchRadius; x < (blockX + searchRadius); x++) {
            for (int z = blockZ - searchRadius; z < (blockZ + searchRadius); z++) {
                for (int y = 127; y >= 0; y--) {
                    final Block toCompare = world.getBlockAt(x, y, z);

                    if (toCompare.getType() == Material.PORTAL
                        && toCompare.getRelative(BlockFace.DOWN).getType() != Material.PORTAL
                    ) {
                        final Location location = toCompare.getLocation();
                        double distance = RayUtil.getRayBetween(location, destination).length();
                        if (distance < minDistance) {
                            minDistance = distance;
                            minLoc = location;
                        }
                    }
                }
            }
        }
        return minLoc;
    }

    @Override
    public Location findOrCreate(Location location) {
        Location existing = findPortal(location);
        if (existing != null) {
            return existing;
        }
        createPortal(location);
        return location; // Bukkit javadoc says to return this even if createPortal fails
    }

    @Override
    public boolean createPortal(Location destination) {
        // TODO: Need impl
        return false;
    }

    private boolean canBuildPortal(Location loc, BlockFace facing) {
        // TODO: Need impl
        return false;
    }
}
