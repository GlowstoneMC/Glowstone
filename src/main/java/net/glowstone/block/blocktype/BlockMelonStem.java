package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class BlockMelonStem extends BlockNeedsAttached {
    private final Random random = new Random();

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        int amount = random.nextInt(4);
        if (amount == 0) {
            return BlockDropless.EMPTY_STACK;
        }
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.MELON_SEEDS, amount)));
    }
}
