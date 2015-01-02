package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockDirectDrops extends BlockNeedsTool {
    private final Material dropType;
    private final short data;
    private final int amount;

    private final MaterialMatcher neededTool;

    public BlockDirectDrops(Material dropType, int data, int amount, MaterialMatcher neededTool) {
        this.dropType = dropType;
        this.amount = amount;
        this.data = (short) data;
        this.neededTool = neededTool;
    }

    public BlockDirectDrops(MaterialMatcher neededTool) {
        this(null, 0, 1, neededTool);
    }

    public BlockDirectDrops(Material dropType, int data, int amount) {
        this(dropType, data, amount, null);
    }

    public BlockDirectDrops(Material dropType, MaterialMatcher neededTool) {
        this(dropType, 0, 1, neededTool);
    }

    public BlockDirectDrops(Material dropType, int amount) {
        this(dropType, 0, amount, null);
    }

    public BlockDirectDrops(Material dropType) {
        this(dropType, 0, 1, null);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(getDrops(block)));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return neededTool;
    }

    private ItemStack getDrops(GlowBlock block) {
        if (dropType == null) {
            return new ItemStack(block.getType(), amount, block.getData());
        } else {
            return new ItemStack(dropType, amount, data);
        }
    }
}
