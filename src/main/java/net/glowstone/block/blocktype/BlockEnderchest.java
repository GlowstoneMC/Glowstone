package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.EnderChest;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockEnderchest extends BlockType {

    public BlockEnderchest() {
        setDrops(new ItemStack(Material.OBSIDIAN, 8));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        // todo: animation?
        player.openInventory(player.getEnderChest());
        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof EnderChest) {
            ((EnderChest) data).setFacingDirection(getOppositeBlockFace(player.getLocation(), false));
            state.setData(data);
        } else {
            warnMaterialData(EnderChest.class, data);
        }
    }
}
