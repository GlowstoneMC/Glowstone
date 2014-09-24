package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Torch;
import org.bukkit.util.Vector;

public class BlockTorch extends BlockType {

    public BlockTorch() {
        setDrops(new ItemStack(Material.TORCH));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        final MaterialData data = state.getData();
        if (data instanceof Torch) {
            ((Torch) data).setFacingDirection(face);
        } else {
            warnMaterialData(Torch.class, data);
        }
    }
}
