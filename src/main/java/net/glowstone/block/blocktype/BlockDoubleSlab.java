package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockDoubleSlab extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.STEP, 2, block.getData())));
    }

}
