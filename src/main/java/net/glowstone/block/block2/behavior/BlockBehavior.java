package net.glowstone.block.block2.behavior;

import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Behavior applicable to a block type.
 */
public interface BlockBehavior {

    void doThing();

    boolean getValue();

    Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool);

}
