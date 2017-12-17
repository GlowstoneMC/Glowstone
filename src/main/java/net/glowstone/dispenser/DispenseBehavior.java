package net.glowstone.dispenser;

import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

public interface DispenseBehavior {

    ItemStack dispense(GlowBlock block, ItemStack stack);
}
