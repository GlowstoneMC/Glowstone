package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockPiston extends BlockType {
    public BlockPiston() {
        setDrops(new ItemStack(Material.PISTON_EXTENSION));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        BlockFace faceHead = calculateFace(player, state); // the direction of the piston head
        state.setRawData((byte) rawFace(faceHead));
    }

    private BlockFace calculateFace(GlowPlayer player, GlowBlockState state) {
        Location pLoc = player.getLocation(); // the location of the player
        Location bLoc = state.getLocation();  // the location of the piston

        if (Math.abs(pLoc.getBlockX() - bLoc.getBlockX()) < 2.0F && Math.abs(pLoc.getBlockZ() - bLoc.getBlockZ()) < 2.0F) {
            double offset = pLoc.getBlockY() + player.getEyeHeight();
            if (offset - (double) bLoc.getBlockY() > 2.0)
                return BlockFace.UP;

            if ((double) bLoc.getBlockY() - offset > 0.0)
                return BlockFace.DOWN;
        }
        return player.getDirection().getOppositeFace();
    }

    private int rawFace(BlockFace face) {
        switch (face) {
            case DOWN: return 0;
            case UP: return 1;
            case NORTH: return 2;
            case SOUTH: return 3;
            case WEST: return 4;
            case EAST: return 5;
        }
        return 0;
    }
}
