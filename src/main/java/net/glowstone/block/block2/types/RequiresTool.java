package net.glowstone.block.block2.types;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.block2.behavior.BaseBlockBehavior;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class RequiresTool extends BaseBlockBehavior {

    private final MaterialMatcher matcher;

    public RequiresTool(MaterialMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (matcher != null && (tool == null || !matcher.matches(tool.getType()))) {
            return NoDrops.EMPTY;
        }
        return super.getDrops(block, tool);
    }
}
