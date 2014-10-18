package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockLeaves extends BlockType {
    private final Random random = new Random();

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        int data = block.getData() % 4; //ignore "non-decay" and "check-decay" data.

        if (tool != null && tool.getType() == Material.SHEARS) {
            return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(block.getType(), 1, (short) data)
            ));
        }

        if (block.getType() == Material.LEAVES_2) {
            data += 4;
        }

        List<ItemStack> drops = new ArrayList<>();

        if (random.nextFloat() < (block.getData() == 3 ? .025f : .05f)) { //jungle leaves drop with 2.5% chance, others drop with 5%
            drops.add(new ItemStack(Material.SAPLING, 1, (short) data));
        }
        if (data == 0 && random.nextFloat() < .005) { //oak leaves have a .5% chance to drop an apple
            drops.add(new ItemStack(Material.APPLE));
        }
        return Collections.unmodifiableList(drops);
    }
}
