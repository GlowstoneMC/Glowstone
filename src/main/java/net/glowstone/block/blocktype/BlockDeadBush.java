package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockDeadBush extends BlockNeedsAttached {

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        Material typeBelow = block.getWorld().getBlockTypeAt(block.getX(), block.getY() - 1, block.getZ());
        switch (typeBelow) {
            case SAND:
            // TODO: colored terracotta
            case LEGACY_STAINED_CLAY:
            case TERRACOTTA:
            case DIRT:
                return true;
            default:
                return false;
        }
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        // If the block below the dead bush is removed,
        // the bush will simply disappear without dropping anything.
        if (tool == null) {
            return Collections.emptyList();
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
