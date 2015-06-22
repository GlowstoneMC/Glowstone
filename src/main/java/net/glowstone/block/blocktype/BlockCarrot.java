package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class BlockCarrot extends BlockCrops {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getData() >= CropState.RIPE.ordinal()) {
            return Collections.unmodifiableList(Collections.singletonList(new ItemStack(Material.CARROT_ITEM, random.nextInt(4) + 1)));
        } else {
            return Collections.unmodifiableList(Collections.singletonList(new ItemStack(Material.CARROT_ITEM, 1)));
        }
    }
}
