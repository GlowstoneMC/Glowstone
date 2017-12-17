package net.glowstone.block.blocktype;

import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.UP;
import static org.bukkit.block.BlockFace.WEST;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockDirectional extends BlockType {

    private final boolean opposite;

    public BlockDirectional(boolean opposite) {
        this.opposite = opposite;
    }

    protected static int getRawFace(BlockFace face) {
        switch (face) {
            case DOWN:
                return 0;
            case UP:
                return 1;
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

    protected static BlockFace getFace(byte raw) {
        switch (raw) {
            case 0:
                return DOWN;
            case 1:
                return UP;
            case 2:
                return NORTH;
            case 3:
                return SOUTH;
            case 4:
                return WEST;
            case 5:
                return EAST;
        }
        return null;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        BlockFace faceHead = calculateFace(player, state); // the direction of the block
        state.setRawData((byte) getRawFace(opposite ? faceHead.getOppositeFace() : faceHead));
    }

    protected BlockFace calculateFace(GlowPlayer player, GlowBlockState state) {
        Location pLoc = player.getLocation(); // the location of the player
        Location bLoc = state.getLocation();  // the location of the block

        if (Math.abs(pLoc.getBlockX() - bLoc.getBlockX()) < 2.0F
            && Math.abs(pLoc.getBlockZ() - bLoc.getBlockZ()) < 2.0F) {
            double offset = pLoc.getBlockY() + player.getEyeHeight();
            if (offset - bLoc.getBlockY() > 2.0) {
                return BlockFace.UP;
            }

            if (bLoc.getBlockY() - offset > 0.0) {
                return DOWN;
            }
        }
        return player.getDirection().getOppositeFace();
    }
}
