package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockSoulSand extends BlockDirectDrops {
    public BlockSoulSand() {
        super(Material.SOUL_SAND, ToolType.SHOVEL);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState blockState, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, blockState, face, holding, clickedLoc);
    }
}
