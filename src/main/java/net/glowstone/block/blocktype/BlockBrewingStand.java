package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.BrewingStandEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class BlockBrewingStand extends BlockContainer {

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new BrewingStandEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    protected Collection<ItemStack> getBlockDrops(GlowBlock block) {
        return Arrays.asList(new ItemStack(Material.BREWING_STAND));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
