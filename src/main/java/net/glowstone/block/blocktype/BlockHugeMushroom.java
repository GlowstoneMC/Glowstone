package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockHugeMushroom extends BlockType {

    private final Material mushroomType;
    private final short data;

    public BlockHugeMushroom(Material mushroomType, short data) {
        this.mushroomType = mushroomType;
        this.data = data;
    }

    public BlockHugeMushroom(Material mushroomType) {
        this(mushroomType, (short) 0);
    }

    public BlockHugeMushroom(boolean isRedMushroom) {
        this(isRedMushroom ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        int rnd = ThreadLocalRandom.current().nextInt(100);
        if (rnd < 80) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(
                Arrays.asList(new ItemStack(mushroomType, rnd > 90 ? 2 : 1, data)));
        }
    }
}
