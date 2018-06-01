package net.glowstone.dispenser;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockDispenser;
import net.glowstone.block.blocktype.BlockLiquid;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemFilledBucket;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        ItemFilledBucket bucket = (ItemFilledBucket) ItemTable.instance().getItem(stack.getType());
        BlockLiquid liquid = (BlockLiquid) bucket.getLiquid();
        BlockFace facing = BlockDispenser.getFacing(block);
        GlowBlock target = block.getRelative(facing);

        if (canPlace(target, facing, stack)) {
            target.setType(liquid.getMaterial());
            stack.setType(Material.BUCKET);
            stack.setAmount(1);
            return stack;
        } else {
            return INSTANCE.dispense(block, stack);
        }
    }

    private boolean canPlace(GlowBlock target, BlockFace facing, ItemStack stack) {
        if (!target.isEmpty()) {
            BlockType targetType = ItemTable.instance().getBlock(target.getType());
            //noinspection ConstantConditions
            if (!targetType.canOverride(target, facing.getOppositeFace(), stack)) {
                return false;
            }
        }

        return true;
    }
}
