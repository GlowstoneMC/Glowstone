package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockStone extends BlockNeedsTool {

    @Override
    public MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        if (block.getType() == Material.STONE) {
            return Arrays.asList(new ItemStack(Material.COBBLESTONE));
        } else {
            return Arrays.asList(new ItemStack(Material.STONE, 1, block.getData()));
        }
    }
}
