package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class BlockAnvil extends BlockFalling {

    public BlockAnvil() {
        super(Material.ANVIL);
        addFunction(Functions.Interact.ANVIL);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        // This is replicated from BlockNeedsTool and has been copy/pasted because classes cannot
        // extend 2 parents
        ToolType neededTool = ToolType.PICKAXE;
        if (tool == null || !neededTool.matches(tool.getType())) {
            return BlockDropless.EMPTY_STACK;
        }

        return getMinedDrops(block);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        ItemStack drop = new ItemStack(Material.ANVIL, 1, (short) (block.getData() / 4));
        return Arrays.asList(drop);
    }
}
