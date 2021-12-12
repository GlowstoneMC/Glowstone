package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockSlab extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        Material blockType = state.getBlock().getType();
        // TODO: 1.13 block type double
        if (blockType == Material.LEGACY_STEP) {
            state.setType(Material.LEGACY_DOUBLE_STEP);
            state.setData(holding.getData());
            return;
        }

        if (face == BlockFace.DOWN || face != BlockFace.UP && clickedLoc.getY() >= 0.5) {
            // TODO: 1.13 block type top
            MaterialData data = state.getData();
            if (data instanceof Step) {
                ((Step) data).setInverted(true);
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
        // TODO: 1.13 new slab types
        return (blockType.name().contains("SLAB"))
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

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@NotNull GlowBlock block, ItemStack tool) {
        // todo: 1.13 new slab types
        if (block.getType() == Material.LEGACY_WOOD_STEP
            || tool != null && ToolType.PICKAXE.matches(tool.getType())) {
            return getMinedDrops(block);
        }
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Arrays.asList(new ItemStack(block.getType(), 1, (short) (block.getData() % 8)));
    }
}
