package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockPiston extends BlockType {
    private final boolean sticky;

    public BlockPiston() {
        sticky = false;
        setDrops(new ItemStack(Material.PISTON_BASE));
    }

    public BlockPiston(boolean sticky) {
        this.sticky = sticky;

        if (sticky) {
            setDrops(new ItemStack(Material.PISTON_STICKY_BASE));
        } else {
            setDrops(new ItemStack(Material.PISTON_BASE));
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        BlockFace faceHead = calculateFace(player, state); // the direction of the piston head
        state.setRawData(getRawFace(faceHead));

        getTargetBlock(state.getBlock()).setType(Material.REDSTONE_BLOCK);

        System.out.println(getTargetBlock(state.getBlock()));
    }

    /**
     * The piston is either non-sticky (default), or has a sticky behavior
     * @return true if the piston has a sticky base
     */
    public boolean isSticky() {
        return sticky;
    }

    /**
     * The block the piston is facing to with its head
     */
    public GlowBlock getTargetBlock(GlowBlock piston) {
        BlockFace face = getBlockFace(piston.getState().getRawData());
        Location target = piston.getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ());
        return (GlowBlock) target.getBlock();
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

    private byte getRawFace(BlockFace face) {
        switch (face) {
            case DOWN: return 0x0;
            case UP: return 0x1;
            case NORTH: return 0x2;
            case SOUTH: return 0x3;
            case WEST: return 0x4;
            case EAST: return 0x5;
        }
        return 0;
    }

    private BlockFace getBlockFace(byte raw) {
        switch (raw) {
            case 0x0:
                return BlockFace.DOWN;
            case 0x1:
                return BlockFace.UP;
            case 0x2:
                return BlockFace.NORTH;
            case 0x3:
                return BlockFace.SOUTH;
            case 0x4:
                return BlockFace.WEST;
            case 0x5:
                return BlockFace.EAST;
        }
        return BlockFace.DOWN;
    }
}
