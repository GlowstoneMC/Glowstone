package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class BlockTallGrass extends BlockType {
    private final Random random = new Random();

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        if (random.nextFloat() < .125) {
            return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.SEEDS, 1)));
        }
        return Collections.unmodifiableList(Arrays.asList(new ItemStack[0]));
    }
}
