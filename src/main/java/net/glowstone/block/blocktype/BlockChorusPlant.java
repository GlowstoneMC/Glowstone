package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockChorusPlant extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return Collections
                .unmodifiableList(Arrays.asList(new ItemStack(Material.CHORUS_FRUIT, 1)));
        } else {
            return Collections.unmodifiableList(Arrays.asList());
        }
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        boolean sideSupport = false;
        for (BlockFace face : SIDES) {
            Block relative = block.getRelative(face);
            if (relative.getType() == Material.CHORUS_PLANT && hasDownSupport(relative)) {
                sideSupport = true;
                break;
            }
        }
        if (sideSupport) {
            boolean upperBlocked = !block.getRelative(BlockFace.UP).isEmpty();
            boolean downBlocked = !block.getRelative(BlockFace.DOWN).isEmpty();
            return !(upperBlocked && downBlocked); //Both of the two can't be blocked
        } else {
            return hasDownSupport(block);
        }
    }

    private boolean hasDownSupport(Block block) {
        Block down = block.getRelative(BlockFace.DOWN);
        return down.getType() == Material.CHORUS_PLANT || down.getType() == Material.ENDER_STONE;
    }
}
