package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockGravel extends BlockFalling {

    public BlockGravel() {
        super(Material.GRAVEL);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(
            ThreadLocalRandom.current().nextInt(10) == 1 ? Material.FLINT : Material.GRAVEL, 1)));
    }

}
