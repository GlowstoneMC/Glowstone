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
        state.setType(getMaterial());
        BlockFace faceHead = calculateFace(player, state); // the direction of the piston head
        state.setRawData(getRawFace(faceHead));
    }

    /**
     * The piston is either non-sticky (default), or has a sticky behavior
     *
     * @return true if the piston has a sticky base
     */
    public boolean isSticky() {
        return sticky;
    }

    /**
     * The block the piston is facing to
     */
    public GlowBlock getFacingBlock(GlowBlock piston, BlockFace head) {
        Location target = piston.getLocation().clone().add(head.getModX(), head.getModY(), head.getModZ());
        return (GlowBlock) target.getBlock();
    }

    @Override
    public void onRedstoneUpdate(GlowBlock block) {
        updatePiston(block);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        updatePiston(block);
    }

    /**
     * Forces the extension state of the piston
     * @see #push to attempt to push the target block
     */
    public void extend(GlowBlock piston) {
        BlockFace head = getBlockFace(piston.getState().getRawData());
        GlowBlock target = getFacingBlock(piston, head);
        target.setType(Material.AIR); // temporary

        //TODO: extend the piston (with the proper block state too)
    }

    public void updatePiston(GlowBlock piston) {
        BlockFace head = getBlockFace(piston.getState().getRawData());
        GlowBlock target = getFacingBlock(piston, head);

        // TODO: piston.isBlockFacePowered(head) is not supported yet.
        // if (piston.isBlockPowered() || piston.isBlockIndirectlyPowered() && (!piston.isBlockFacePowered(head))) {
        if (piston.isBlockPowered() || piston.isBlockIndirectlyPowered()) {
            if (target != null) {
                push(piston);
            } else {
                extend(piston);
            }
        } else {
            // TODO: retract piston
        }
    }

    /**
     * If possible, the piston will extend and push the target block (if present)
     */
    public void push(GlowBlock piston) {
        // TODO: push the target block
        BlockFace direction = getBlockFace(piston.getState().getRawData());
        int blocksToPush = 0;
        boolean canPush = false;

        for (int i = 1; i < 12 + 1; i++) {
            Location location = piston.getLocation().clone().add(direction.getModX() * i, direction.getModY() * i, direction.getModZ() * i);
            GlowBlock target = (GlowBlock) location.getBlock();

            if (target == null || target.getType() == Material.AIR) {
                blocksToPush = i;
                canPush = true;
                break;
            }
        }

        // TODO: clean this code, it's very messy...
        if (canPush) {
            for (int count = blocksToPush - 1; count >= 0; count--) {
                int i = count + 1;
                Location location = piston.getLocation().clone().add(direction.getModX() * i, direction.getModY() * i, direction.getModZ() * i);
                location.getBlock().setType(piston.getLocation().clone().add(direction.getModX() * (i - 1), direction.getModY() * (i - 1), direction.getModZ() * (i - 1)).getBlock().getType());
                location.getBlock().getState().setRawData(piston.getLocation().clone().add(direction.getModX() * (i - 1), direction.getModY() * (i - 1), direction.getModZ() * (i - 1)).getBlock().getState().getRawData());
            }
            extend(piston);
        }

    }

    public void retract(GlowBlock piston) {
        if (sticky) {
            // TODO: bring target block back
        }
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
            case DOWN:
                return 0x0;
            case UP:
                return 0x1;
            case NORTH:
                return 0x2;
            case SOUTH:
                return 0x3;
            case WEST:
                return 0x4;
            case EAST:
                return 0x5;
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
