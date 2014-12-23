package net.glowstone.block.block2.types;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.block2.behavior.BaseBlockBehavior;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class DirtDrops extends BaseBlockBehavior {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        // TODO: check variant instead of magic value
        int data = block.getData() == 1 ? 1 : 0;
        return Arrays.asList(new ItemStack(Material.DIRT, 1, ((short) data)));
    }
}
