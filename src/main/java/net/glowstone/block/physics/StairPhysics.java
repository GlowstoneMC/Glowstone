package net.glowstone.block.physics;


import org.bukkit.block.BlockFace;

public class StairPhysics extends DefaultBlockPhysics {
    
    @Override
    public int getPlacedMetadata(int current, BlockFace against) {
        switch (against) {
        case NORTH:
        default:
            return 0x0;
        case SOUTH:
            return 0x1;
        case EAST:
            return 0x2;
        case WEST:
            return 0x3;
        }
    }
}
