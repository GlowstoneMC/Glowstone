package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class BlockDeadBush extends BlockNeedsAttached {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        int typeIdBelow = block.getWorld()
            .getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ());
        switch (Material.getMaterial(typeIdBelow)) {
            case SAND:
            case STAINED_CLAY:
            case HARD_CLAY:
            case DIRT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        // If the block below the dead bush is removed,
        // the bush will simply disappear without dropping anything.
        if (tool == null) {
            return BlockDropless.EMPTY_STACK;
        }

        // Dead bush drops it self when broken with shears
        if (tool.getType().equals(Material.SHEARS)) {
            return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.DEAD_BUSH)));
        }

        // Dead bush drops 0-2 sticks when broken without shears
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Collections.unmodifiableList(Arrays.asList(
            new ItemStack(Material.STICK,random.nextInt(3))));
    }
}
