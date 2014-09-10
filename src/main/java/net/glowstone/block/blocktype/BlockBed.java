package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockBed extends BlockType {

    public BlockBed() {
        setDrops(new ItemStack(Material.BED));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        BlockFace direction = getOppositeBlockFace(player.getLocation(), false).getOppositeFace();
        if (state.getBlock().getRelative(direction).getType() == Material.AIR && state.getBlock().getRelative(direction).getRelative(BlockFace.DOWN).getType().isSolid()) {
            super.placeBlock(player, state, face, holding, clickedLoc);
            final MaterialData data = state.getData();
            if (data instanceof Bed) {
                ((Bed) data).setFacingDirection(direction);
                state.setData(data);
            } else {
                warnMaterialData(Bed.class, data);
            }
        }
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        if (block.getType() == Material.BED_BLOCK) {
            BlockFace direction = ((Bed) block.getState().getData()).getFacing();
            GlowBlock headBlock = block.getRelative(direction);
            headBlock.setType(Material.BED_BLOCK);
            GlowBlockState headBlockState = headBlock.getState();
            MaterialData data = headBlockState.getData();
            ((Bed) data).setHeadOfBed(true);
            ((Bed) data).setFacingDirection(direction);
            headBlockState.setData(data);
            headBlockState.update(true);
        }
    }
}
