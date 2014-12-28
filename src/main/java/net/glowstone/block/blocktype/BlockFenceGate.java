package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockFenceGate extends BlockOpenable {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData materialData = state.getData();
        if (materialData instanceof Gate) {
            Gate gate = (Gate) materialData;
            float yaw = player.getLocation().getYaw();
            gate.setFacingDirection(blockFaceFromYaw(yaw));
            state.update(true);
        } else {
            warnMaterialData(Gate.class, materialData);
        }
    }

    private static BlockFace blockFaceFromYaw(float yaw) {
        // nb: opposite from getOppositeBlockFace in BlockType
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360.0;
        }
        if (0 <= yaw && yaw < 45) {
            return BlockFace.SOUTH;
        } else if (45 <= yaw && yaw < 135) {
            return BlockFace.WEST;
        } else if (135 <= yaw && yaw < 225) {
            return BlockFace.NORTH;
        } else if (225 <= yaw && yaw < 315) {
            return BlockFace.EAST;
        } else if (315 <= yaw && yaw < 360.0) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.EAST;
        }
    }

    @Override
    protected void onOpened(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc, GlowBlockState state, MaterialData materialData) {
        if (materialData instanceof Gate) {
            Gate gate = (Gate) materialData;
            gate.setFacingDirection(getOpenDirection(player.getLocation().getYaw(), gate.getFacing()));
        } else {
            warnMaterialData(Gate.class, materialData);
        }
    }

    private static BlockFace getOpenDirection(float yaw, BlockFace oldFacing) {
        BlockFace facingDirection = blockFaceFromYaw(yaw);

        if (facingDirection == oldFacing.getOppositeFace()) {
            return facingDirection;
        } else {
            return oldFacing;
        }
    }
}
