package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.block.GlowBlock;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockPotato extends BlockCrops {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getData() >= CropState.RIPE.ordinal()) {
            if (ThreadLocalRandom.current().nextInt(100) < 2) {
                return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(Material.POTATO_ITEM, ThreadLocalRandom.current().nextInt(4) + 1),
                    new ItemStack(Material.POISONOUS_POTATO, 1)));
            } else {
                return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(Material.POTATO_ITEM,
                        ThreadLocalRandom.current().nextInt(4) + 1)));
            }
        } else {
            return Collections
                .unmodifiableList(Arrays.asList(new ItemStack(Material.POTATO_ITEM, 1)));
        }
    }
}
