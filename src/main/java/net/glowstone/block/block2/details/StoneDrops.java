package net.glowstone.block.block2.details;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.StoneType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class StoneDrops extends BaseBlockBehavior {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        StoneType type = (StoneType) block.getNewState().getPropertyValue("variant").get();
        if (type == StoneType.NORMAL) {
            return Arrays.asList(new ItemStack(Material.COBBLESTONE));
        } else {
            return Arrays.asList(new ItemStack(Material.STONE, 1, type.getData()));
        }
    }
}
