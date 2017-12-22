package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockDirectDrops extends BlockNeedsTool {

    private final Material dropType;
    private final short data;
    private final int amount;

    private final MaterialMatcher neededTool;

    /**
     * Creates an instance.
     * @param dropType the type of item to drop when broken
     * @param data the damage or block-data value of the dropped item
     * @param amount the amount to drop
     * @param neededTool the tool(s) that can break this block
     */
    public BlockDirectDrops(Material dropType, int data, int amount, MaterialMatcher neededTool) {
        this.dropType = dropType;
        this.amount = amount;
        this.data = (short) data;
        this.neededTool = neededTool;
    }

    /**
     * Creates an instance that drops nothing when broken.
     * @param neededTool the tool(s) that can break this block
     */
    public BlockDirectDrops(MaterialMatcher neededTool) {
        this(null, 0, 1, neededTool);
    }

    /**
     * Creates an instance that can be broken without a tool.
     * @param dropType the type of item to drop when broken
     * @param data the damage or block-data value of the dropped item
     * @param amount the amount to drop
     */
    public BlockDirectDrops(Material dropType, int data, int amount) {
        this(dropType, data, amount, null);
    }

    /**
     * Creates an instance that drops 1 item with damage or block data 0 when broken.
     * @param dropType the type of item to drop when broken
     * @param neededTool the tool(s) that can break this block
     */
    public BlockDirectDrops(Material dropType, MaterialMatcher neededTool) {
        this(dropType, 0, 1, neededTool);
    }

    /**
     * Creates an instance that can be broken without a tool, and that drops items with damage or
     * block data 0 when broken.
     * @param dropType the type of item to drop when broken
     * @param amount the amount to drop
     */
    public BlockDirectDrops(Material dropType, int amount) {
        this(dropType, 0, amount, null);
    }


    /**
     * Creates an instance that can be broken without a tool, and that drops 1 item with damage or
     * block data 0 when broken.
     * @param dropType the type of item to drop when broken
     */
    public BlockDirectDrops(Material dropType) {
        this(dropType, 0, 1, null);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Collections.unmodifiableList(Arrays.asList(getDrops(block)));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
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
