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

public class BlockCarrot extends BlockCrops {

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getData() >= CropState.RIPE.ordinal()) {
            return Collections.unmodifiableList(Arrays.asList(
                new ItemStack(Material.CARROT, ThreadLocalRandom.current().nextInt(4) + 1)));
        } else {
            return Collections
                .unmodifiableList(Arrays.asList(new ItemStack(Material.CARROT, 1)));
        }
    }
}
