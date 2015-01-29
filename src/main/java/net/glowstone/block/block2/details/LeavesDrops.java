package net.glowstone.block.block2.details;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.block2.sponge.BlockState;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LeavesDrops extends BaseBlockBehavior {
    private final Random random = new Random();

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        BlockState state = block.getNewState();
        TreeSpecies species = (TreeSpecies) state.getPropertyValue("variant").get();

        if (tool != null && tool.getType() == Material.SHEARS) {
            return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(block.getType(), 1, species.getData())
            ));
        }

        List<ItemStack> drops = new ArrayList<>();
        if (random.nextFloat() < (block.getData() == 3 ? .025f : .05f)) { // jungle leaves drop with 2.5% chance, others drop with 5%
            drops.add(new ItemStack(Material.SAPLING, 1, species.getData()));
        }
        if (species == TreeSpecies.GENERIC && random.nextFloat() < .005) { // oak leaves have a .5% chance to drop an apple
            drops.add(new ItemStack(Material.APPLE));
        }
        return Collections.unmodifiableList(drops);
    }
}
