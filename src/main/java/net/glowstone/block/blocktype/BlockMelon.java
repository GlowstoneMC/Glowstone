package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockMelon extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Collections.singletonList(new ItemStack(Material.MELON, random.nextInt(5) + 3)));
    }
}
