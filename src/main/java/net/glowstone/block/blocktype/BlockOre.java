package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BlockOre extends BlockNeedsTool {

    private final Material dropType;
    private final MaterialMatcher neededTool;
    private final int minCount, maxCount;
    private final int data;

    public BlockOre(Material dropType, MaterialMatcher neededTool, int data, int minCount,
        int maxCount) {
        this.dropType = dropType;
        this.neededTool = neededTool;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.data = data;
    }

    public BlockOre(Material dropType, MaterialMatcher neededTool, int data, int count) {
        this(dropType, neededTool, data, count, count);
    }

    public BlockOre(Material dropType, MaterialMatcher neededTool, int data) {
        this(dropType, neededTool, data, 1);
    }

    public BlockOre(Material dropType, MaterialMatcher neededTool) {
        this(dropType, neededTool, 0, 1);
    }

    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = minCount;
        if (maxCount > minCount) {
            count += random.nextInt(maxCount - minCount);
        }
        ItemStack stack = new ItemStack(dropType, count, (short) data);
        if (tool == null) {
            return Collections.unmodifiableList(Arrays.asList(stack));
        }
        Collection<ItemStack> drops = super.getDrops(block, tool);
        if (drops.size() == 0) {
            return drops;
        }
        if (tool.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            stack.setAmount(count * getMultiplicator(random,
                tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
        }
        return Collections.unmodifiableList(Arrays.asList(stack));
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return getDrops(block, null);
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return neededTool;
    }

    private int getMultiplicator(Random random, int level) {
        if (level <= 0) {
            return 1;
        }
        double r = random.nextDouble();
        if (level == 1) {
            if (r < 0.33) {
                return 2;
            }
        }
        if (level == 2) {
            if (r < 0.25) {
                return 2;
            }
            if (r < 0.5) {
                return 3;
            }
        }
        if (level == 3) {
            if (r < 0.2) {
                return 2;
            }
            if (r < 0.4) {
                return 3;
            }
            if (r < 0.6) {
                return 4;
            }
        }
        return 1;
    }
}
