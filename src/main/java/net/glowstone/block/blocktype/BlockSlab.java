package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.util.Vector;

public class BlockSlab extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        if (state.getBlock().getType() == Material.STEP) {
            state.setType(Material.DOUBLE_STEP);
            state.setRawData((byte) holding.getDurability());
            return;
        } else if (state.getBlock().getType() == Material.WOOD_STEP) {
            state.setType(Material.WOOD_DOUBLE_STEP);
            state.setRawData((byte) holding.getDurability());
            return;
        }

        super.placeBlock(player, state, face, holding, clickedLoc);

        if (face == BlockFace.DOWN || (face != BlockFace.UP && clickedLoc.getY() >= 8.0D)) {
            MaterialData data = state.getData();
            if ((data instanceof Step)) {
                ((Step) data).setInverted(true);
            } else if ((data instanceof WoodenStep)) {
                ((WoodenStep) data).setInverted(true);
            }
            state.setData(data);
        }
    }

    private boolean matchingType(GlowBlock block, BlockFace face, ItemStack holding, boolean ignoreFace) {
        byte blockData = block.getData();
        byte holdingData = (byte) holding.getDurability();
        return (block.getType() == Material.STEP || block.getType() == Material.WOOD_STEP) &&
                block.getType() == holding.getType() &&
                ((face == BlockFace.UP && blockData == holdingData) ||
                        (face == BlockFace.DOWN && blockData - 8 == holdingData) ||
                        (ignoreFace && blockData % 8 == holdingData));
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return matchingType(block, face, holding, true);
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return matchingType(block, face, holding, false);
    }
}
