package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockDoor extends BlockType {

    public BlockDoor(Material itemMaterial) {
        setDrops(new ItemStack(itemMaterial));
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        GlowBlock topHalf = block.getRelative(BlockFace.UP);
        if (!topHalf.isEmpty()) {
            BlockType type = ItemTable.instance().getBlock(topHalf.getType());
            if (!type.canOverride(topHalf, BlockFace.UP, null)) {
                return false;
            }
        }
        return against == BlockFace.UP;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        // remove the other half of the door
        GlowBlockState state = block.getState();
        MaterialData data = state.getData();

        if (data instanceof Door) {
            Door door = (Door) data;
            if (door.isTopHalf()) {
                Block b = block.getRelative(BlockFace.DOWN);
                if (b.getType() == block.getType())
                    b.setType(Material.AIR);
            } else {
                Block b = block.getRelative(BlockFace.UP);
                if (b.getType() == block.getType())
                    b.setType(Material.AIR);
            }
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        // place the door and calculate the facing
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (!(data instanceof Door)) {
            warnMaterialData(Door.class, data);
            return;
        }
        BlockFace facing = player.getDirection();
        ((Door) data).setFacingDirection(facing.getOppositeFace());

        // modify facing for double-doors
        GlowBlock leftBlock = null;
        switch (facing) {
            case NORTH:
                leftBlock = state.getBlock().getRelative(BlockFace.WEST);
                break;
            case WEST:
                leftBlock = state.getBlock().getRelative(BlockFace.SOUTH);
                break;
            case SOUTH:
                leftBlock = state.getBlock().getRelative(BlockFace.EAST);
                break;
            case EAST:
                leftBlock = state.getBlock().getRelative(BlockFace.NORTH);
                break;
        }

        if (leftBlock != null && leftBlock.getState().getData() instanceof Door) {
            switch (facing) {
                case NORTH:
                    data.setData((byte) 6);
                    break;
                case WEST:
                    data.setData((byte) 5);
                    break;
                case SOUTH:
                    data.setData((byte) 4);
                    break;
                case EAST:
                    data.setData((byte) 7);
                    break;
            }
        }

        // place top half of door
        GlowBlockState topState = state.getBlock().getRelative(BlockFace.UP).getState();
        topState.setType(state.getType());
        MaterialData topData = topState.getData();
        if (!(topData instanceof Door)) {
            warnMaterialData(Door.class, data);
        } else {
            ((Door) topData).setTopHalf(true);
            topState.update(true);
        }
    }

    /**
     * Opens and closes the door when right-clicked by the player.
     */
    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        // handles opening and closing the door
        if (block.getType() == Material.IRON_DOOR_BLOCK)
            return false;

        GlowBlockState state = block.getState();
        MaterialData data = state.getData();

        if (data instanceof Door) {
            Door door = (Door) data;
            if (door.isTopHalf()) {
                door = null;
                block = block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ());
                state = block.getState();
                data = state.getData();
                if (data instanceof Door) {
                    door = (Door) data;
                }
            }

            if (door != null)
                door.setOpen(!door.isOpen());

            state.update(true);
        }

        return true;
    }

}
