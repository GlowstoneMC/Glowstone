package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockIronTrapDoor extends BlockType {
    private BlockTrapDoor trapDoor;

    public BlockIronTrapDoor() {
        trapDoor = new BlockTrapDoor(this);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        trapDoor.placeBlock(player, state, face, holding, clickedLoc);
    }
}
