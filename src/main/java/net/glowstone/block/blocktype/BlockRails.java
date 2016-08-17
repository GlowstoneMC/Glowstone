package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;

public class BlockRails extends BlockNeedsAttached {
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(block.getType()));
    }

    private static BlockFace blockFaceFromYaw(float yaw) {
        // nb: opposite from getOppositeBlockFace in BlockType
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360.0;
        }
        if (yaw < 45 || yaw >= 315) {
            return BlockFace.NORTH;
        } else if (45 <= yaw && yaw < 135) {
            return BlockFace.EAST;
        } else if (135 <= yaw && yaw < 225) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.WEST;
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData materialData = state.getData();
        if (materialData instanceof Rails) {
            Rails rails = (Rails) materialData;
            float yaw = player.getLocation().getYaw();
            rails.setDirection(blockFaceFromYaw(yaw), false);
            state.update(true);
        } else {
            warnMaterialData(Rails.class, materialData);
        }
    }
}
