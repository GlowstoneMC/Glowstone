package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockStairs extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Stairs) {
            ((Stairs) data).setFacingDirection(player.getCardinalFacing());

            if (face == BlockFace.DOWN || face != BlockFace.UP && clickedLoc.getY() >= 0.5) {
                ((Stairs) data).setInverted(true);
            }

            state.setData(data);
        } else {
            warnMaterialData(Stairs.class, data);
        }
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@NotNull GlowBlock block, ItemStack tool) {
        if (isWoodenStair(block.getType())
            || tool != null && ToolType.PICKAXE.matches(tool.getType())) {
            return getMinedDrops(block);
        }
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Arrays.asList(new ItemStack(block.getType()));
    }

    private boolean isWoodenStair(Material type) {
        switch (type) {
            case ACACIA_STAIRS:
            case BIRCH_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_STAIRS:
            case SPRUCE_STAIRS:
            case OAK_STAIRS:
                return true;
            default:
                return false;
        }
    }
}
