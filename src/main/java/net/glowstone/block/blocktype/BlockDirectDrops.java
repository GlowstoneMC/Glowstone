package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockDirectDrops extends BlockNeedsTool {

    private final Material dropType;
    private final short data;
    private final int amount;

    private final MaterialMatcher neededTool;

    /**
     * Creates a block type that drops an item directly when broken.
     * @param dropType the type of item to drop when broken, or null to drop nothing
     * @param data the damage or block-data value of the dropped item
     * @param amount the amount to drop
     * @param neededTool the tool(s) that can break this block, or null if breakable without a tool
     */
    public BlockDirectDrops(Material dropType, int data, int amount, MaterialMatcher neededTool) {
        this.dropType = dropType;
        this.amount = amount;
        this.data = (short) data;
        this.neededTool = neededTool;
    }

    /**
     * Creates a block type that drops nothing when broken.
     * @param neededTool the tool(s) that can break this block
     */
    public BlockDirectDrops(MaterialMatcher neededTool) {
        this(null, 0, 1, neededTool);
    }

    /**
     * Creates a block type that drops an item directly when broken, and can be broken without a
     * tool.
     * @param dropType the type of item to drop when broken
     * @param data the damage or block-data value of the dropped item
     * @param amount the amount to drop
     */
    public BlockDirectDrops(Material dropType, int data, int amount) {
        this(dropType, data, amount, null);
    }

    /**
     * Creates a block type that directly drops 1 copy of an item with damage or block data 0 when
     * broken.
     * @param dropType the type of item to drop when broken
     * @param neededTool the tool(s) that can break this block
     */
    public BlockDirectDrops(Material dropType, MaterialMatcher neededTool) {
        this(dropType, 0, 1, neededTool);
    }

    /**
     * Creates a block type that directly drops an item with damage or block data 0 when broken, and
     * can be broken without a tool.
     * @param dropType the type of item to drop when broken
     * @param amount the amount to drop
     */
    public BlockDirectDrops(Material dropType, int amount) {
        this(dropType, 0, amount, null);
    }


    /**
     * Creates a block type that directly drops 1 copy of an item with damage or block data 0 when
     * broken, and can be broken without a tool.
     * @param dropType the type of item to drop when broken
     */
    public BlockDirectDrops(Material dropType) {
        this(dropType, 0, 1, null);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Collections.unmodifiableList(Arrays.asList(getDrops(block)));
    }

    @Override
    public MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return neededTool;
    }

    protected ItemStack getDrops(GlowBlock block) {
        if (dropType == null) {
            return new ItemStack(block.getType(), amount, block.getData());
        } else {
            return new ItemStack(dropType, amount, data);
        }
    }
}
