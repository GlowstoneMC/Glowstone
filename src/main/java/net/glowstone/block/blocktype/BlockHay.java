package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockHay extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        switch (face) {
            case NORTH:
            case SOUTH:
                state.setRawData((byte) 8);
                break;
            case WEST:
            case EAST:
                state.setRawData((byte) 4);
                break;
            case UP:
            case DOWN:
                state.setRawData((byte) 0);
                break;
        }
    }
}
