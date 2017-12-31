package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.BrewingStandEntity;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockBrewingStand extends BlockContainer {

    @Override
    public BlockEntity createBlockEntity(GlowBlock block) {
        return new BrewingStandEntity(block);
    }

    @Override
    protected Collection<ItemStack> getBlockDrops(GlowBlock block) {
        return Arrays.asList(new ItemStack(Material.BREWING_STAND_ITEM));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
