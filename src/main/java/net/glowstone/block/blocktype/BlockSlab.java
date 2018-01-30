package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.util.Vector;

public class BlockSlab extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        Material blockType = state.getBlock().getType();
        if (blockType == Material.STEP) {
            state.setType(Material.DOUBLE_STEP);
            state.setData(holding.getData());
            return;
        } else if (blockType == Material.WOOD_STEP) {
            state.setType(Material.WOOD_DOUBLE_STEP);
            state.setData(holding.getData());
            return;
        } else if (blockType == Material.STONE_SLAB2) {
            state.setType(Material.DOUBLE_STONE_SLAB2);
            state.setData(holding.getData());
            return;
        } else if (blockType == Material.PURPUR_SLAB) {
            state.setType(Material.PURPUR_DOUBLE_SLAB);
            state.setData(holding.getData());
            return;
        }

        if (face == BlockFace.DOWN || face != BlockFace.UP && clickedLoc.getY() >= 0.5) {
            MaterialData data = state.getData();
            if (data instanceof Step) {
                ((Step) data).setInverted(true);
            } else if (data instanceof WoodenStep) {
                ((WoodenStep) data).setInverted(true);
            } else if (data.getItemType() == Material.STONE_SLAB2
                || data.getItemType() == Material.PURPUR_SLAB) {
                Step slab = new Step(data.getItemType());
                slab.setInverted(true);
                data = slab;
            }
            state.setData(data);
        }
    }

    private boolean matchingType(GlowBlock block, BlockFace face, ItemStack holding,
        boolean ignoreFace) {
        if (holding == null) {
            return false;
        }
        Material blockType = block.getType();
        byte blockData = block.getData();
        byte holdingData = holding.getData().getData();
        return (blockType == Material.STEP || blockType == Material.WOOD_STEP
                        || blockType == Material.STONE_SLAB2 || blockType == Material.PURPUR_SLAB)
                && blockType == holding.getType()
                && (face == BlockFace.UP && blockData == holdingData
                        || face == BlockFace.DOWN && blockData - 8 == holdingData
                        || ignoreFace && blockData % 8 == holdingData);
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return matchingType(block, face, holding, true);
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return matchingType(block, face, holding, false);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getType() == Material.WOOD_STEP
                || tool != null && ToolType.PICKAXE.matches(tool.getType())) {
            return getMinedDrops(block);
        }
        return BlockDropless.EMPTY_STACK;
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Arrays.asList(new ItemStack(block.getType(), 1, (short) (block.getData() % 8)));
    }
}
