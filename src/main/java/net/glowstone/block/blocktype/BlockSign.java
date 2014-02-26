package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockSign extends BlockType {

    public BlockSign() {
        setDrops(new ItemStack(Material.SIGN));
    }

    public boolean isWallSign() {
        return getMaterial() == Material.WALL_SIGN;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        state.setType(getMaterial());
        byte data;
        if (isWallSign()) {
            // attach to appropriate side of wall
            data = getFacing(face);
        } else {
            // calculate the facing of the sign based on the angle between the player and the post
            Location loc = player.getLocation();
            double dx = state.getX() + 0.5 - loc.getX();
            double dz = state.getZ() + 0.5 - loc.getZ();
            double angle = Math.atan2(dz, dx);
            double part = angle * 8 / Math.PI;
            data = (byte)(Math.round(part + 20) % 16);
        }
        state.setRawData(data);
    }

    private byte getFacing(BlockFace face) {
        switch (face) {
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
        }
        return 0;
    }
}
