package net.glowstone.block.blocktype;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class BlockDoubleSlab extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getType() == Material.WOOD_DOUBLE_STEP ||
                (tool != null && ToolType.PICKAXE.matches(tool.getType()))) {
            return getMinedDrops(block);
        }
        return BlockDropless.EMPTY_STACK;
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Collections.singletonList(getDrops(block));
    }

    private static ItemStack getDrops(GlowBlock block) {
        switch (block.getType()) {
            case WOOD_DOUBLE_STEP:
                return new ItemStack(Material.WOOD_STEP, 2, (short) (block.getData() % 8));
            case DOUBLE_STEP:
                return new ItemStack(Material.STEP, 2, (short) (block.getData() % 8));
            case DOUBLE_STONE_SLAB2:
                return new ItemStack(Material.STONE_SLAB2, 2);
        }
        GlowServer.logger.warning("BlockDoubleSlab got wrong material: " + block.getType());
        return new ItemStack(Material.STEP, 2);
    }

}
