package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Torch;
import org.bukkit.util.Vector;

public class BlockTorch extends BlockNeedsAttached {

    public BlockTorch() {
        setDrops(new ItemStack(Material.TORCH));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (data instanceof Torch) {
            if (canAttachTo(state.getBlock(), face)) {
                ((Torch) data).setFacingDirection(face);
            } else {
                ((Torch) data).setFacingDirection(BlockFace.UP);
            }
        } else {
            warnMaterialData(Torch.class, data);
        }
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
                           GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    protected BlockFace getAttachedFace(GlowBlock me) {
        return ((Torch) me.getState().getData()).getAttachedFace();
    }
}
