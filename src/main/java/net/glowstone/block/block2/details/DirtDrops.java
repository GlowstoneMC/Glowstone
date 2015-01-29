package net.glowstone.block.block2.details;

import net.glowstone.block.GlowBlock;
import org.bukkit.DirtType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class DirtDrops extends BaseBlockBehavior {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        DirtType type = (DirtType) block.getNewState().getPropertyValue("variant").get();
        if (type != DirtType.COARSE) {
            type = DirtType.NORMAL;
        }
        return Arrays.asList(new ItemStack(Material.DIRT, 1, type.getData()));
    }
}
