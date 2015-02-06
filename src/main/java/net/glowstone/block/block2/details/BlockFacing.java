package net.glowstone.block.block2.details;

public enum BlockFacing {
    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static final BlockFacing[] CARDINAL = {NORTH, SOUTH, WEST, EAST};
    public static final BlockFacing[] NOT_DOWN = {UP, NORTH, SOUTH, WEST, EAST};
}
