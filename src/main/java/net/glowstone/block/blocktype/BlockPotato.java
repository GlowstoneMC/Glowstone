package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockPotato extends BlockCrops {

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getData() >= CropState.RIPE.ordinal()) {
            if (ThreadLocalRandom.current().nextInt(100) < 2) {
                return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(Material.POTATO, ThreadLocalRandom.current().nextInt(4) + 1),
                    new ItemStack(Material.POISONOUS_POTATO, 1)));
            } else {
                return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(Material.POTATO,
                        ThreadLocalRandom.current().nextInt(4) + 1)));
            }
        } else {
            return Collections
                .unmodifiableList(Arrays.asList(new ItemStack(Material.POTATO, 1)));
        }
    }
}
