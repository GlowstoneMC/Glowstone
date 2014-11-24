package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class BlockPumpkinStem extends BlockType {
    private final Random random = new Random();

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.PUMPKIN_SEEDS, random.nextInt(4))));
    }
}
