package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Stone;
import org.bukkit.material.types.StoneType;

public class BlockStone extends BlockNeedsTool {

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        if (((Stone) block.getState().getData()).getType() == StoneType.NORMAL) {
            return Arrays.asList(new ItemStack(Material.COBBLESTONE));
        } else {
            return Arrays.asList(new ItemStack(Material.STONE, 1, block.getData()));
        }
    }
}
