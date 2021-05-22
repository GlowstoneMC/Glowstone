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
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
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
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType,
                               byte newData) {
        if (newType != Material.AIR) {
            return;
        }

        if (oldType.getData() == Door.class) {
            Door door = new Door(oldType);
            door.setData(oldData);
            if (door.isTopHalf()) {
                Block b = block.getRelative(BlockFace.DOWN);
                if (b.getState().getData() instanceof Door) {
                    b.setType(Material.AIR);
                }
            } else {
                Block b = block.getRelative(BlockFace.UP);
                if (b.getState().getData() instanceof Door) {
                    b.setType(Material.AIR);
                }
            }
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        // place the door and calculate the facing
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (!(data instanceof Door)) {
            warnMaterialData(Door.class, data);
            return;
        }

        BlockFace facing = player.getCardinalFacing();
        ((Door) data).setFacingDirection(facing.getOppositeFace());

        // modify facing for double-doors
        GlowBlock leftBlock = null;
        byte newDirectionData = 0;
        switch (facing) {
            case NORTH:
                leftBlock = state.getBlock().getRelative(BlockFace.WEST);
                newDirectionData = 6;
                break;
            case WEST:
                leftBlock = state.getBlock().getRelative(BlockFace.SOUTH);
                newDirectionData = 5;
                break;
            case SOUTH:
                leftBlock = state.getBlock().getRelative(BlockFace.EAST);
                newDirectionData = 4;
                break;
            case EAST:
                leftBlock = state.getBlock().getRelative(BlockFace.NORTH);
                newDirectionData = 7;
                break;
            default:
                // do nothing
                // TODO: Does this lead to the correct behavior when player is facing up or down?
        }

        if (leftBlock != null && leftBlock.getState().getData() instanceof Door) {
            data.setData(newDirectionData);
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
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        // handles opening and closing the door
        if (block.getType() == Material.IRON_DOOR) {
            return false;
        }

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

            if (door != null) {
                door.setOpen(!door.isOpen());
            }

            state.update(true);
        }

        return true;
    }

    @Override
    public void onRedstoneUpdate(GlowBlock block) {
        GlowBlockState state = block.getState();
        Door door = (Door) state.getData();
        if (!door.isTopHalf()) {

            boolean powered = block.isBlockIndirectlyPowered();
            if (powered != door.isOpen()) {
                door.setOpen(powered);
                state.update();
            }
        }
    }

}
